package com.isxcode.star.agent.run;

import com.alibaba.fastjson2.JSON;
import com.isxcode.star.api.agent.constants.AgentType;
import com.isxcode.star.api.agent.pojos.req.DeployContainerReq;
import com.isxcode.star.api.agent.pojos.req.YagExecuteWorkReq;
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
    public String getMaster(String sparkHomePath) {

        String sparkHome;
        if (!Strings.isEmpty(sparkHomePath)) {
            sparkHome = sparkHomePath;
        } else {
            sparkHome = System.getenv("SPARK_HOME");
        }
        String defaultSparkConfig = sparkHome + "/conf/spark-defaults.conf";

        try (BufferedReader reader = new BufferedReader(new FileReader(defaultSparkConfig))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.matches("^spark\\.master\\s+(.+)")) {
                    return line.split("\\s+")[1];
                }
            }
            throw new IsxAppException("无法获取master url");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException("无法获取master url");
        }
    }

    public String getMasterWebUrl(String sparkHomePath) {

        String sparkHome;
        if (!Strings.isEmpty(sparkHomePath)) {
            sparkHome = sparkHomePath;
        } else {
            sparkHome = System.getenv("SPARK_HOME");
        }
        String defaultSparkConfig = sparkHome + "/conf/spark-defaults.conf";

        try (BufferedReader reader = new BufferedReader(new FileReader(defaultSparkConfig))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.matches("^spark\\.master\\.web\\.url\\s+(.+)")) {
                    return line.split("\\s+")[1];
                }
            }
            throw new IsxAppException("无法获取master url");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException("无法获取master url");
        }
    }

    @Override
    public SparkLauncher genSparkLauncher(YagExecuteWorkReq yagExecuteWorkReq) {

        String appName = "zhiqingyun-" + yagExecuteWorkReq.getWorkType() + "-" + yagExecuteWorkReq.getWorkId() + "-"
            + yagExecuteWorkReq.getWorkInstanceId();

        SparkLauncher sparkLauncher;
        if (WorkType.SPARK_JAR.equals(yagExecuteWorkReq.getWorkType())) {
            // 如果是自定义作业
            sparkLauncher = new SparkLauncher().setVerbose(false)
                .setMainClass(yagExecuteWorkReq.getSparkSubmit().getMainClass()).setDeployMode("cluster")
                .setAppName(yagExecuteWorkReq.getSparkSubmit().getAppName() + "-" + yagExecuteWorkReq.getWorkType()
                    + "-" + yagExecuteWorkReq.getWorkId() + "-" + yagExecuteWorkReq.getWorkInstanceId())
                .setMaster(getMaster(yagExecuteWorkReq.getSparkHomePath()))
                .setAppResource(yagExecuteWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                    + yagExecuteWorkReq.getSparkSubmit().getAppResource())
                .setSparkHome(yagExecuteWorkReq.getAgentHomePath() + File.separator + "spark-min");
        } else {
            sparkLauncher = new SparkLauncher().setVerbose(false)
                .setMainClass(yagExecuteWorkReq.getSparkSubmit().getMainClass()).setDeployMode("cluster")
                .setAppName(appName).setMaster(getMaster(yagExecuteWorkReq.getSparkHomePath()))
                .setAppResource(yagExecuteWorkReq.getAgentHomePath() + File.separator + "plugins" + File.separator
                    + yagExecuteWorkReq.getSparkSubmit().getAppResource())
                .setSparkHome(yagExecuteWorkReq.getAgentHomePath() + File.separator + "spark-min");
        }

        // 添加额外依赖
        if (yagExecuteWorkReq.getLibConfig() != null) {
            yagExecuteWorkReq.getLibConfig().forEach(e -> {
                String libPath =
                    yagExecuteWorkReq.getAgentHomePath() + File.separator + "file" + File.separator + e + ".jar";
                sparkLauncher.addJar(libPath);
            });
        }

        // 添加自定义函数
        if (yagExecuteWorkReq.getPluginReq().getCsvFilePath() != null) {
            sparkLauncher.addFile(yagExecuteWorkReq.getPluginReq().getCsvFilePath());
        }

        // 添加自定义函数
        if (yagExecuteWorkReq.getFuncConfig() != null) {
            yagExecuteWorkReq.getFuncConfig().forEach(e -> {
                String libPath = yagExecuteWorkReq.getAgentHomePath() + File.separator + "file" + File.separator
                    + e.getFileId() + ".jar";
                sparkLauncher.addJar(libPath);
            });
        }

        if (!Strings.isEmpty(yagExecuteWorkReq.getAgentHomePath())) {
            File[] jarFiles = new File(yagExecuteWorkReq.getAgentHomePath() + File.separator + "lib").listFiles();
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

        if (WorkType.SPARK_JAR.equals(yagExecuteWorkReq.getWorkType())) {
            sparkLauncher.addAppArgs(yagExecuteWorkReq.getArgs());
        } else {
            sparkLauncher.addAppArgs(Base64.getEncoder()
                .encodeToString(yagExecuteWorkReq.getPluginReq() == null ? yagExecuteWorkReq.getArgsStr().getBytes()
                    : JSON.toJSONString(yagExecuteWorkReq.getPluginReq()).getBytes()));
        }

        yagExecuteWorkReq.getSparkSubmit().getConf().forEach(sparkLauncher::setConf);

        return sparkLauncher;
    }

    @Override
    public String executeWork(SparkLauncher sparkLauncher) throws IOException {

        Process launch = sparkLauncher.launch();
        InputStream inputStream = launch.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        StringBuilder errLog = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            errLog.append(line).append("\n");
            String pattern = "(driver-\\d+-\\d+)\\s+is\\s+(\\w+)";
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(line);
            if (matcher.find()) {
                return matcher.group().replace(" is RUNNING", "").replace(" is FINISHED", "")
                    .replace(" is SUBMITTED", "").replace(" is FAILED", "");
            }
        }

        try {
            int exitCode = launch.waitFor();
            if (exitCode == 1) {
                throw new IsxAppException(errLog.toString());
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
    public String getAppStatus(String submissionId, String sparkHomePath) throws IOException {

        Document doc = Jsoup.connect(getMasterWebUrl(sparkHomePath)).get();

        Element completedDriversTable = doc.selectFirst(".aggregated-completedDrivers table");
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
    public String getAppLog(String submissionId, String sparkHomePath) throws IOException {

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
    public String getStdoutLog(String submissionId, String sparkHomePath) throws IOException {

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
    public String getAppData(String submissionId, String sparkHomePath) throws IOException {

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
    public void killApp(String submissionId, String sparkHomePath, String agentHomePath) throws IOException {

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

    @Override
    public String getAgentName() {
        return AgentType.StandAlone;
    }

    @Override
    public SparkLauncher genSparkLauncher(DeployContainerReq deployContainerReq) throws IOException {

        String appName = "zhiqingyun-SPARK_CONTAINER-" + deployContainerReq.getContainerId();

        SparkLauncher sparkLauncher = new SparkLauncher().setVerbose(false)
            .setMainClass(deployContainerReq.getSparkSubmit().getMainClass()).setDeployMode("cluster")
            .setAppName(appName).setMaster(getMaster(deployContainerReq.getSparkHomePath()))
            .setAppResource(deployContainerReq.getAgentHomePath() + File.separator + "plugins" + File.separator
                + deployContainerReq.getSparkSubmit().getAppResource())
            .setSparkHome(deployContainerReq.getAgentHomePath() + File.separator + "spark-min");

        if (!Strings.isEmpty(deployContainerReq.getAgentHomePath())) {
            File[] jarFiles = new File(deployContainerReq.getAgentHomePath() + File.separator + "lib").listFiles();
            if (jarFiles != null) {
                for (File jar : jarFiles) {
                    try {
                        // 使用本地hive驱动
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

        sparkLauncher.addAppArgs(Base64.getEncoder()
            .encodeToString(deployContainerReq.getPluginReq() == null ? deployContainerReq.getArgs().getBytes()
                : JSON.toJSONString(deployContainerReq.getPluginReq()).getBytes()));

        deployContainerReq.getSparkSubmit().getConf().forEach(sparkLauncher::setConf);

        return sparkLauncher;
    }
}
