package com.isxcode.star.plugin.dataSync.jdbc;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.agent.req.PluginReq;
import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.api.func.constants.FuncType;
import com.isxcode.star.api.plugin.constants.OverModeType;
import com.isxcode.star.api.work.constants.SetMode;
import org.apache.logging.log4j.util.Strings;
import org.apache.spark.SparkConf;
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
            pluginReq.getSyncWorkConfig().getSourceTableColumn().forEach(e -> {
                sourceColTranslateSql.put(e.getCode(), e.getSql());
            });

            // 封装字段信息
            List<String> sourceCols = new ArrayList<>();
            List<String> targetCols = new ArrayList<>();
            pluginReq.getSyncWorkConfig().getColumnMap().forEach(e -> {
                sourceCols.add(
                    Strings.isEmpty(sourceColTranslateSql.get(e.getSource())) ? String.format("`%s`", e.getSource())
                        : sourceColTranslateSql.get(e.getSource()) + " ");
                targetCols.add(String.format("`%s`", e.getTarget()));
            });

            // 判断是覆盖还是新增
            String insertSql =
                OverModeType.OVERWRITE.equals(pluginReq.getSyncWorkConfig().getOverMode()) ? "insert overwrite"
                    : "insert into";

            // 执行sql同步语句
            sparkSession.sql(insertSql + " table " + targetTempView + " ( " + Strings.join(targetCols, ',')
                + " ) select " + Strings.join(sourceCols, ',') + " from " + sourceTempView);
        }
    }

    /**
     * 构建来源视图.
     */
    public static String genSourceTempView(SparkSession sparkSession, PluginReq conf) {

        String sourceTableName = "zhiqingyun_src_" + conf.getSyncWorkConfig().getSourceDatabase().getDbTable();

        if (DatasourceType.HIVE.equals(conf.getSyncWorkConfig().getSourceDBType())) {

            return parseHiveDatabase(conf.getSyncWorkConfig().getSourceDatabase().getUrl())
                + conf.getSyncWorkConfig().getSourceDatabase().getDbTable();
        } else {

            Properties prop = new Properties();
            prop.put("user", conf.getSyncWorkConfig().getSourceDatabase().getUser());
            if (conf.getSyncWorkConfig().getSourceDatabase().getPassword() != null) {
                prop.put("password", conf.getSyncWorkConfig().getSourceDatabase().getPassword());
            }

            prop.put("driver", conf.getSyncWorkConfig().getSourceDatabase().getDriver());

            // 创建一个 ArrayList 存储分区条件
            List<String> predicates = new ArrayList<>();

            // 生成查询条件并添加到列表中
            for (int i = 0; i < conf.getSyncRule().getNumPartitions(); i++) {
                // 不同的数据库要使用各自支持hash函数
                predicates.add(getHashPredicate(conf.getSyncWorkConfig().getSourceDBType(),
                    conf.getSyncWorkConfig().getPartitionColumn(), conf.getSyncRule().getNumPartitions(), i, i));
            }

            // 将列表转换为字符串数组
            String[] predicate = predicates.toArray(new String[0]);

            String dbTable = DatasourceType.POSTGRE_SQL.equals(conf.getSyncWorkConfig().getSourceDBType())
                ? "\"" + conf.getSyncWorkConfig().getSourceDatabase().getDbTable() + "\""
                : conf.getSyncWorkConfig().getSourceDatabase().getDbTable();

            // 拼接来源的条件
            if (Strings.isNotEmpty(conf.getSyncWorkConfig().getQueryCondition())) {
                dbTable = "( select * from " + dbTable + " where " + conf.getSyncWorkConfig().getQueryCondition()
                    + " ) as " + sourceTableName;
            }
            Dataset<Row> source = sparkSession.read().jdbc(conf.getSyncWorkConfig().getSourceDatabase().getUrl(),
                dbTable, predicate, prop);

            source.createOrReplaceTempView(sourceTableName);
        }

        return sourceTableName;
    }

    /**
     * 构建去向视图.
     */
    public static String genTargetTempView(SparkSession sparkSession, PluginReq conf) {

        String targetTableName = "zhiqingyun_dist_" + conf.getSyncWorkConfig().getTargetDatabase().getDbTable();

        if (DatasourceType.HIVE.equals(conf.getSyncWorkConfig().getTargetDBType())) {

            return parseHiveDatabase(conf.getSyncWorkConfig().getTargetDatabase().getUrl())
                + conf.getSyncWorkConfig().getTargetDatabase().getDbTable();
        } else {

            String dbTable = DatasourceType.POSTGRE_SQL.equals(conf.getSyncWorkConfig().getTargetDBType())
                ? "\"" + conf.getSyncWorkConfig().getTargetDatabase().getDbTable() + "\""
                : conf.getSyncWorkConfig().getTargetDatabase().getDbTable();

            DataFrameReader frameReader = sparkSession.read().format("jdbc")
                .option("driver", conf.getSyncWorkConfig().getTargetDatabase().getDriver())
                .option("url", conf.getSyncWorkConfig().getTargetDatabase().getUrl()).option("dbtable", dbTable)
                .option("user", conf.getSyncWorkConfig().getTargetDatabase().getUser()).option("truncate", "true");

            if (conf.getSyncWorkConfig().getTargetDatabase().getPassword() != null) {
                frameReader.option("password", conf.getSyncWorkConfig().getTargetDatabase().getPassword());
            }

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

    public static String getHashPredicate(String datasourceType, String PartitionColumn, Integer NumPartitions,
        Integer startIndex, Integer endIndex) {

        switch (datasourceType) {
            case DatasourceType.MYSQL:
            case DatasourceType.TIDB:
                return "CRC32(`" + PartitionColumn + "`) % " + NumPartitions + " in (" + startIndex + ",-" + endIndex
                    + ")";
            case DatasourceType.ORACLE:
                return "MOD(ORA_HASH(\"" + PartitionColumn + "\")," + NumPartitions + ") in (" + startIndex + ",-"
                    + endIndex + ")";
            case DatasourceType.OCEANBASE:
            case DatasourceType.DM:
                return "ORA_HASH(`" + PartitionColumn + "`) % " + NumPartitions + " in (" + startIndex + ",-" + endIndex
                    + ")";
            case DatasourceType.SQL_SERVER:
                return "CHECKSUM(" + PartitionColumn + ") % " + NumPartitions + " in (" + startIndex + ",-" + endIndex
                    + ")";
            case DatasourceType.POSTGRE_SQL:
                return "hashtext(\"" + PartitionColumn + "\") % " + NumPartitions + " in (" + startIndex + ",-"
                    + endIndex + ")";
            case DatasourceType.CLICKHOUSE:
                return "sipHash64(`" + PartitionColumn + "`) % " + NumPartitions + " in (" + startIndex + ",-"
                    + endIndex + ")";
            case DatasourceType.HIVE:
                return "hash(`" + PartitionColumn + "`) % " + NumPartitions + " in (" + startIndex + ",-" + endIndex
                    + ")";
            case DatasourceType.HANA_SAP:
                return "MOD(HASH_SHA256(`" + PartitionColumn + "`), " + NumPartitions + ") in (" + startIndex + ",-"
                    + endIndex + ")";
            case DatasourceType.DORIS:
            case DatasourceType.STAR_ROCKS:
                return "murmur_hash3_32(`" + PartitionColumn + "`) % " + NumPartitions + " in (" + startIndex + ",-"
                    + endIndex + ")";
            case DatasourceType.DB2:
                return "MOD(hash8(`" + PartitionColumn + "`)," + NumPartitions + ") in (" + startIndex + ",-" + endIndex
                    + ")";
            case DatasourceType.H2:
                return "MOD(ORA_HASH(`" + PartitionColumn + "`), " + NumPartitions + ") in (" + startIndex + ",-"
                    + endIndex + ")";
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
