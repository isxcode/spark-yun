package com.isxcode.star.agent.run.impl;

import com.alibaba.fastjson2.JSON;
import com.isxcode.star.agent.run.AgentService;
import com.isxcode.star.api.agent.constants.AgentType;
import com.isxcode.star.api.agent.req.SubmitWorkReq;
import com.isxcode.star.api.work.constants.WorkType;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.logging.log4j.util.Strings;
import org.apache.spark.launcher.SparkLauncher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class StandaloneAgentService implements AgentService {

    @Override
    public String getAgentType() {
        return AgentType.StandAlone;
    }

    @Override
    public String getMaster(String sparkHomePath) {

        String sparkHome = !Strings.isEmpty(sparkHomePath) ? sparkHomePath : System.getenv("SPARK_HOME");
        String defaultSparkConfig = sparkHome + "/conf/spark-defaults.conf";

        try (BufferedReader reader = new BufferedReader(new FileReader(defaultSparkConfig))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.matches("^spark\\.master\\s+(.+)")) {
                    return line.split("\\s+")[1];
                }
            }
            throw new IsxAppException("没有配置master url");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException("无法获取master url");
        }
    }

    public String getMasterWebUrl(String sparkHomePath) {

        String sparkHome = !Strings.isEmpty(sparkHomePath) ? sparkHomePath : System.getenv("SPARK_HOME");
        String defaultSparkConfig = sparkHome + "/conf/spark-defaults.conf";

        try (BufferedReader reader = new BufferedReader(new FileReader(defaultSparkConfig))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.matches("^spark\\.master\\.web\\.url\\s+(.+)")) {
                    return line.split("\\s+")[1];
                }
            }
            throw new IsxAppException("没有配置master web url");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException("无法获取master web url");
        }
    }

    @Override
    public SparkLauncher getSparkLauncher(SubmitWorkReq submitWorkReq) {

        SparkLauncher sparkLauncher =
            new SparkLauncher().setVerbose(false).setMainClass(submitWorkReq.getSparkSubmit().getMainClass())
                .setMaster(getMaster(submitWorkReq.getSparkHomePath()))
                .setSparkHome(submitWorkReq.getAgentHomePath() + File.separator + "spark-min");

        if (!WorkType.PY_SPARK.equals(submitWorkReq.getWorkType())) {
            sparkLauncher.setDeployMode("cluster");
        }

        if (WorkType.SPARK_JAR.equals(submitWorkReq.getWorkType())) {
            sparkLauncher
                .setAppName(submitWorkReq.getSparkSubmit().getAppName() + "-" + submitWorkReq.getWorkType() + "-"
                    + submitWorkReq.getWorkId() + "-" + submitWorkReq.getWorkInstanceId())
                .setAppResource(submitWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                    + submitWorkReq.getSparkSubmit().getAppResource());
        } else if (WorkType.PY_SPARK.equals(submitWorkReq.getWorkType())) {
            sparkLauncher
                .setAppName("zhiqingyun-" + submitWorkReq.getWorkType() + "-" + submitWorkReq.getWorkId() + "-"
                    + submitWorkReq.getWorkInstanceId())
                .setAppResource(submitWorkReq.getAgentHomePath() + File.separator + "works" + File.separator
                    + submitWorkReq.getWorkInstanceId() + ".py");
        } else {
            sparkLauncher
                .setAppName("zhiqingyun-" + submitWorkReq.getWorkType() + "-" + submitWorkReq.getWorkId() + "-"
                    + submitWorkReq.getWorkInstanceId())
                .setMaster(getMaster(submitWorkReq.getSparkHomePath())).setAppResource(submitWorkReq.getAgentHomePath()
                    + File.separator + "plugins" + File.separator + submitWorkReq.getSparkSubmit().getAppResource());
        }

        // 添加额外依赖
        if (submitWorkReq.getLibConfig() != null) {
            submitWorkReq.getLibConfig().forEach(e -> sparkLauncher
                .addJar(submitWorkReq.getAgentHomePath() + File.separator + "file" + File.separator + e + ".jar"));
        }

        // 添加Excel/csv文件
        if (submitWorkReq.getPluginReq().getCsvFilePath() != null) {
            sparkLauncher.addFile(submitWorkReq.getPluginReq().getCsvFilePath());
        }

        // 添加自定义函数
        if (submitWorkReq.getFuncConfig() != null) {
            submitWorkReq.getFuncConfig().forEach(e -> sparkLauncher.addJar(
                submitWorkReq.getAgentHomePath() + File.separator + "file" + File.separator + e.getFileId() + ".jar"));
        }

        // 添加至轻云的jar
        if (!Strings.isEmpty(submitWorkReq.getAgentHomePath())) {
            File[] jarFiles = new File(submitWorkReq.getAgentHomePath() + File.separator + "lib").listFiles();
            if (jarFiles != null) {
                for (File jar : jarFiles) {
                    try {
                        if (!jar.getName().contains("hive") && !jar.getName().contains("zhiqingyun-agent.jar")) {
                            sparkLauncher.addJar(jar.toURI().toURL().toString());
                        }
                    } catch (MalformedURLException e) {
                        log.error(e.getMessage(), e);
                        throw new IsxAppException("50010", "添加lib中文件异常", e.getMessage());
                    }
                }
            }
        }

        // 压缩请求的args
        if (WorkType.SPARK_JAR.equals(submitWorkReq.getWorkType())) {
            sparkLauncher.addAppArgs(submitWorkReq.getArgs());
        } else {
            sparkLauncher.addAppArgs(Base64.getEncoder()
                .encodeToString(submitWorkReq.getPluginReq() == null ? submitWorkReq.getArgsStr().getBytes()
                    : JSON.toJSONString(submitWorkReq.getPluginReq()).getBytes()));
        }

        // 删除自定义属性
        submitWorkReq.getSparkSubmit().getConf().remove("qing.hive.username");

        // 将提交的spark配置加入到sparkLauncher
        submitWorkReq.getSparkSubmit().getConf().forEach(sparkLauncher::setConf);

        return sparkLauncher;
    }

    @Override
    public String submitWork(SparkLauncher sparkLauncher) throws Exception {

        Process launch = sparkLauncher.launch();
        InputStream errorStream = launch.getErrorStream();
        InputStream inputStream = launch.getInputStream();
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream, StandardCharsets.UTF_8));
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        StringBuilder errLog = new StringBuilder();
        StringBuilder inputLog = new StringBuilder();
        String line;
        while ((line = errorReader.readLine()) != null) {
            errLog.append(line).append("\n");
            String pattern = "(driver-\\d+-\\d+)\\s+is\\s+(\\w+)";
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(line);
            if (matcher.find()) {
                return matcher.group().replace(" is RUNNING", "").replace(" is FINISHED", "")
                    .replace(" is SUBMITTED", "").replace(" is FAILED", "");
            }
        }

        while ((line = inputReader.readLine()) != null) {
            inputLog.append(line).append("\n");
        }

        try {
            int exitCode = launch.waitFor();
            if (exitCode == 1) {
                throw new IsxAppException(errLog + "\n" + inputLog);
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        } finally {
            launch.destroy();
        }

        throw new IsxAppException("无法获取submissionId \n" + errLog);
    }

    @Override
    public String getWorkStatus(String submissionId, String sparkHomePath) throws Exception {

        Document doc = Jsoup.connect(getMasterWebUrl(sparkHomePath)).get();

        Element completedDriversTable = doc.selectFirst(".aggregated-completedDrivers table");
        if (completedDriversTable == null) {
            throw new IsxAppException("检测不到应用信息");
        }
        Elements completedDriversRows = completedDriversTable.select("tbody tr");

        Element runningDriversTable = doc.selectFirst(".aggregated-activeDrivers table");
        Elements runningDriversRows = runningDriversTable.select("tbody tr");

        Map<String, String> apps = new HashMap<>();

        for (Element row : completedDriversRows) {
            apps.put(row.selectFirst("td:nth-child(1)").text(), row.selectFirst("td:nth-child(4)").text());
        }

        for (Element row : runningDriversRows) {
            apps.put(row.selectFirst("td:nth-child(1)").text().replace(" (kill)", ""), row.select("td").get(3).text());
        }

        return apps.get(submissionId);
    }

    @Override
    public String getStderrLog(String submissionId, String sparkHomePath) throws Exception {

        Document doc = Jsoup.connect(getMasterWebUrl(sparkHomePath)).get();

        Element completedDriversTable = doc.selectFirst(".aggregated-completedDrivers table");
        Elements completedDriversRows = completedDriversTable.select("tbody tr");

        Map<String, String> apps = new HashMap<>();

        for (Element row : completedDriversRows) {
            if (row.select("td:nth-child(3) a").first() != null) {
                apps.put(row.selectFirst("td:nth-child(1)").text(),
                    row.select("td:nth-child(3) a").first().attr("href"));
            }
        }
        String workUrl = apps.get(submissionId);

        doc = Jsoup.connect(workUrl).get();
        Elements rows = doc.select(".aggregated-finishedDrivers table tbody tr");
        Map<String, String> driversMap = new HashMap<>();
        for (Element row : rows) {
            String driverId = row.select("td:nth-child(1)").text();
            String stderrUrl = row.select("td:nth-child(7) a[href$=stderr]").attr("href");
            driversMap.put(driverId, stderrUrl);
        }

        String errlogUrl = driversMap.get(submissionId);
        doc = Jsoup.connect(errlogUrl).get();
        Element preElement = doc.selectFirst("pre");

        return preElement.text();
    }

    @Override
    public String getStdoutLog(String submissionId, String sparkHomePath) throws Exception {

        Document doc = Jsoup.connect(getMasterWebUrl(sparkHomePath)).get();

        Element completedDriversTable = doc.selectFirst(".aggregated-activeDrivers table");
        Elements completedDriversRows = completedDriversTable.select("tbody tr");

        Map<String, String> apps = new HashMap<>();

        for (Element row : completedDriversRows) {
            if (row.select("td:nth-child(3) a").first() != null) {
                String metaSubmissionId = row.selectFirst("td:nth-child(1)").text().replace("(kill)", "").trim();
                apps.put(metaSubmissionId, row.select("td:nth-child(3) a").first().attr("href"));
            }
        }
        String workUrl = apps.get(submissionId);

        doc = Jsoup.connect(workUrl).get();
        Elements rows = doc.select(".aggregated-runningDrivers table tbody tr");
        Map<String, String> driversMap = new HashMap<>();
        for (Element row : rows) {
            String driverId = row.select("td:nth-child(1)").text();
            String stderrUrl = row.select("td:nth-child(7) a[href$=stdout]").attr("href");
            driversMap.put(driverId, stderrUrl);
        }

        String errlogUrl = driversMap.get(submissionId);
        doc = Jsoup.connect(errlogUrl).get();
        Element preElement = doc.selectFirst("pre");

        return preElement.text();
    }

    @Override
    public String getCustomWorkStdoutLog(String appId, String sparkHomePath) throws Exception {

        Document doc = Jsoup.connect(getMasterWebUrl(sparkHomePath)).get();

        Element completedDriversTable = doc.selectFirst(".aggregated-completedDrivers table");
        Elements completedDriversRows = completedDriversTable.select("tbody tr");

        Map<String, String> apps = new HashMap<>();

        for (Element row : completedDriversRows) {
            if (row.select("td:nth-child(3) a").first() != null) {
                String metaSubmissionId = row.selectFirst("td:nth-child(1)").text().replace("(kill)", "").trim();
                apps.put(metaSubmissionId, row.select("td:nth-child(3) a").first().attr("href"));
            }
        }
        String workUrl = apps.get(appId);

        doc = Jsoup.connect(workUrl).get();
        Elements rows = doc.select(".aggregated-finishedDrivers table tbody tr");
        Map<String, String> driversMap = new HashMap<>();
        for (Element row : rows) {
            String driverId = row.select("td:nth-child(1)").text().trim();
            String stderrUrl = row.select("td:nth-child(7) a[href$=stdout]").attr("href");
            driversMap.put(driverId, stderrUrl);
        }

        String errlogUrl = driversMap.get(appId);
        doc = Jsoup.connect(errlogUrl).get();
        Element preElement = doc.selectFirst("pre");

        return preElement.text();
    }

    @Override
    public String getWorkDataStr(String submissionId, String sparkHomePath) throws Exception {

        Document doc = Jsoup.connect(getMasterWebUrl(sparkHomePath)).get();

        Element completedDriversTable = doc.selectFirst(".aggregated-completedDrivers table");
        Elements completedDriversRows = completedDriversTable.select("tbody tr");

        Map<String, String> apps = new HashMap<>();

        for (Element row : completedDriversRows) {
            Element first = row.select("td:nth-child(3) a").first();
            if (first != null) {
                apps.put(row.selectFirst("td:nth-child(1)").text(), first.attr("href"));
            }
        }

        String workUrl = apps.get(submissionId);

        doc = Jsoup.connect(workUrl).get();
        Elements rows = doc.select(".aggregated-finishedDrivers table tbody tr");
        Map<String, String> driversMap = new HashMap<>();
        for (Element row : rows) {
            String driverId = row.select("td:nth-child(1)").text();
            String stderrUrl = row.select("td:nth-child(7) a[href$=stdout]").attr("href");
            driversMap.put(driverId, stderrUrl);
        }

        String errlogUrl = driversMap.get(submissionId);
        doc = Jsoup.connect(errlogUrl).get();
        Element preElement = doc.selectFirst("pre");

        return preElement.text().replace("LogType:spark-yun", "").replace("End of ", "");
    }

    @Override
    public void stopWork(String submissionId, String sparkHomePath, String agentHomePath) throws Exception {

        if (Strings.isEmpty(sparkHomePath) || "null".equals(sparkHomePath)) {
            sparkHomePath = agentHomePath + "/zhiqingyun-agent/spark-min";
        }

        String url = getMasterWebUrl(sparkHomePath) + "/driver/kill/";

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        String payload = "id=" + submissionId + "&terminate=true";
        StringEntity stringEntity = new StringEntity(payload, ContentType.APPLICATION_FORM_URLENCODED);

        httpPost.setEntity(stringEntity);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                EntityUtils.toString(responseEntity);
            }
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException("中止失败");
        }
    }
}
