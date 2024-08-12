package com.isxcode.star.plugin.excelSync.jdbc;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.agent.constants.AgentType;
import com.isxcode.star.api.agent.pojos.req.PluginReq;
import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.api.func.constants.FuncType;
import com.isxcode.star.api.plugin.OverModeType;
import com.isxcode.star.api.work.constants.SetMode;
import org.apache.logging.log4j.util.Strings;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkFiles;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Execute {

    public static void main(String[] args) {

        PluginReq pluginReq = parse(args);

        try (SparkSession sparkSession = initSparkSession(pluginReq.getSparkConfig())) {

            // 注册自定义函数
            if (pluginReq.getFuncInfoList() != null) {
                pluginReq.getFuncInfoList().forEach(e -> {
                    if (FuncType.UDF.equals(e.getType())) {
                        sparkSession.udf().registerJava(e.getFuncName(), e.getClassName(),
                            getResultType(e.getResultType()));
                    } else if (FuncType.UDAF.equals(e.getType())) {
                        sparkSession.udf().registerJavaUDAF(e.getFuncName(), e.getClassName());
                    }
                });
            }

            // 创建来源表视图
            String sourceTempView = genSourceTempView(sparkSession, pluginReq);

            // 创建去向表视图
            String targetTempView = genTargetTempView(sparkSession, pluginReq);

            // 来源字段的转换sql收集
            Map<String, String> sourceColTranslateSql = new HashMap<>();
            pluginReq.getExcelSyncConfig().getSourceTableColumn().forEach(e -> {
                sourceColTranslateSql.put(e.getCode(), e.getSql());
            });

            // 封装字段信息
            List<String> sourceCols = new ArrayList<>();
            List<String> targetCols = new ArrayList<>();
            pluginReq.getExcelSyncConfig().getColumnMap().forEach(e -> {
                sourceCols.add(
                    Strings.isEmpty(sourceColTranslateSql.get(e.getSource())) ? String.format("`%s`", e.getSource())
                        : sourceColTranslateSql.get(e.getSource()) + " ");
                targetCols.add(String.format("`%s`", e.getTarget()));
            });

            // 判断是覆盖还是新增
            String insertSql =
                OverModeType.OVERWRITE.equals(pluginReq.getExcelSyncConfig().getOverMode()) ? "insert overwrite"
                    : "insert into";

            // 执行sql同步语句
            if (Strings.isNotEmpty(pluginReq.getExcelSyncConfig().getQueryCondition())) {
                sparkSession.sql(insertSql + " table " + targetTempView + " ( " + Strings.join(targetCols, ',')
                    + " ) select " + Strings.join(sourceCols, ',') + " from " + sourceTempView + " where "
                    + pluginReq.getExcelSyncConfig().getQueryCondition());
            } else {
                sparkSession.sql(insertSql + " table " + targetTempView + " ( " + Strings.join(targetCols, ',')
                    + " ) select " + Strings.join(sourceCols, ',') + " from " + sourceTempView);
            }
        }
    }

    /**
     * 构建来源视图.
     */
    public static String genSourceTempView(SparkSession sparkSession, PluginReq conf) {

        String sourceTableName = "zhiqingyun_src_" + conf.getExcelSyncConfig().getSourceFileId();

        Map<String, String> optionsMap = new HashMap<>();

        // 永远都会有表头
        optionsMap.put("header", "true");
        optionsMap.put("delimiter", ";");
        optionsMap.put("encoding", "UTF-8");
        optionsMap.put("sep", ",");
        optionsMap.put("quote", "\"");

        // 翻译不同平台的地址
        String csvFilePath;
        if (AgentType.K8S.equals(conf.getAgentType())) {
            csvFilePath = "file://" + conf.getCsvFilePath();
        } else if (AgentType.YARN.equals(conf.getAgentType())) {
            String rootDirectory = SparkFiles.getRootDirectory();
            String[] split = rootDirectory.split("/");
            csvFilePath = "/user/" + split[5] + "/.sparkStaging/" + split[7] + "/" + conf.getCsvFileName();
        } else {
            csvFilePath = SparkFiles.get(conf.getCsvFileName());
        }

        Dataset<Row> source = sparkSession.read().options(optionsMap).csv(csvFilePath);
        source.createOrReplaceTempView(sourceTableName);

        return sourceTableName;
    }

    /**
     * 构建去向视图.
     */
    public static String genTargetTempView(SparkSession sparkSession, PluginReq conf) {

        String targetTableName = "zhiqingyun_dist_" + conf.getExcelSyncConfig().getTargetDatabase().getDbTable();

        if (DatasourceType.HIVE.equals(conf.getExcelSyncConfig().getTargetDBType())) {

            return parseHiveDatabase(conf.getExcelSyncConfig().getTargetDatabase().getUrl())
                + conf.getExcelSyncConfig().getTargetDatabase().getDbTable();
        } else {
            DataFrameReader frameReader = sparkSession.read().format("jdbc")
                .option("driver", conf.getExcelSyncConfig().getTargetDatabase().getDriver())
                .option("url", conf.getExcelSyncConfig().getTargetDatabase().getUrl())
                .option("dbtable", conf.getExcelSyncConfig().getTargetDatabase().getDbTable())
                .option("user", conf.getExcelSyncConfig().getTargetDatabase().getUser())
                .option("password", conf.getExcelSyncConfig().getTargetDatabase().getPassword())
                .option("truncate", "true");

            if (SetMode.ADVANCE.equals(conf.getSyncRule().getSetMode())) {
                conf.getSyncRule().getSqlConfig().forEach(frameReader::option);
            }
            Dataset<Row> source = frameReader.load();
            source.createOrReplaceTempView(targetTableName);
        }

        return targetTableName;
    }

    public static PluginReq parse(String[] args) {
        if (args.length == 0) {
            throw new RuntimeException("args is empty");
        }
        return JSON.parseObject(Base64.getDecoder().decode(args[0]), PluginReq.class);
    }

    public static SparkConf initSparkConf(Map<String, String> sparkConfig) {
        SparkConf conf = new SparkConf();
        if (sparkConfig != null) {
            for (Map.Entry<String, String> entry : sparkConfig.entrySet()) {
                conf.set(entry.getKey(), entry.getValue());
            }
        }
        return conf;
    }

    public static SparkSession initSparkSession(Map<String, String> sparkConfig) {

        SparkSession.Builder sparkSessionBuilder = SparkSession.builder();

        SparkConf conf = initSparkConf(sparkConfig);

        if (Strings.isEmpty(sparkConfig.get("hive.metastore.uris"))) {
            return sparkSessionBuilder.config(conf).getOrCreate();
        } else {
            return sparkSessionBuilder.config(conf).enableHiveSupport().getOrCreate();
        }
    }

    public static String parseHiveDatabase(String jdbcUrl) {

        Pattern pattern = Pattern.compile("^jdbc:hive2://.*?/(.*?)$");
        Matcher matcher = pattern.matcher(jdbcUrl);
        if (matcher.find()) {
            return matcher.group(1) + ".";
        } else {
            return "";
        }
    }

    public static String getHash(String datasourceType) {
        switch (datasourceType) {
            case DatasourceType.MYSQL:
            case DatasourceType.TIDB:
                return "CRC32";
            case DatasourceType.ORACLE:
            case DatasourceType.OCEANBASE:
            case DatasourceType.DM:
                return "ORA_HASH";
            case DatasourceType.SQL_SERVER:
                return "CHECKSUM";
            case DatasourceType.POSTGRE_SQL:
                return "md5";
            case DatasourceType.CLICKHOUSE:
                return "sipHash64";
            case DatasourceType.HIVE:
                return "hash";
            case DatasourceType.HANA_SAP:
                return "HASH_SHA256";
            case DatasourceType.DORIS:
            case DatasourceType.STAR_ROCKS:
                return "murmur_hash3_32";
            case DatasourceType.DB2:
                return "hash8";
            default:
                throw new RuntimeException("暂不支持的数据库");
        }
    }

    private static DataType getResultType(String resultType) {
        switch (resultType) {
            case "string":
                return DataTypes.StringType;
            case "int":
                return DataTypes.IntegerType;
            case "long":
                return DataTypes.LongType;
            case "double":
                return DataTypes.DoubleType;
            case "boolean":
                return DataTypes.BooleanType;
            case "date":
                return DataTypes.DateType;
            case "timestamp":
                return DataTypes.TimestampType;
            default:
                return DataTypes.StringType;
        }
    }

}
