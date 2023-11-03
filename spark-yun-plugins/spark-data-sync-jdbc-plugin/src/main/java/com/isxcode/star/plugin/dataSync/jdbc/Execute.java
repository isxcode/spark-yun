package com.isxcode.star.plugin.dataSync.jdbc;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.agent.pojos.req.PluginReq;
import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.api.plugin.OverModeType;
import com.isxcode.star.api.work.constants.SetMode;
import org.apache.logging.log4j.util.Strings;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.*;

public class Execute {

	public static void main(String[] args) {

		PluginReq conf = parse(args);

		if (DatasourceType.HIVE.equals(conf.getSyncWorkConfig().getTargetDBType())
				|| DatasourceType.HIVE.equals(conf.getSyncWorkConfig().getSourceDBType())) {
			conf.getSparkConfig().put("hive.metastore.uris", "thrift://localhost:9083");
		}

		try (SparkSession sparkSession = initSparkSession(conf.getSparkConfig())) {

			// 创建来源表视图
			String sourceTempView = genSourceTempView(sparkSession, conf);

			// 创建去向表视图
			String targetTempView = genTargetTempView(sparkSession, conf);

			// 来源字段的转换sql收集
			Map<String, String> sourceColTranslateSql = new HashMap<>();
			conf.getSyncWorkConfig().getSourceTableColumn().forEach(e -> {
				sourceColTranslateSql.put(e.getCode(), e.getSql());
			});

			// 封装字段信息
			List<String> sourceCols = new ArrayList<>();
			List<String> targetCols = new ArrayList<>();
			conf.getSyncWorkConfig().getColumnMap().forEach(e -> {
				sourceCols.add(Strings.isEmpty(sourceColTranslateSql.get(e.getSource()))
						? String.format("`%s`", e.getSource())
						: sourceColTranslateSql.get(e.getSource()));
				targetCols.add(String.format("`%s`", e.getTarget()));
			});

			// 判断是覆盖还是新增
			String insertSql = OverModeType.OVERWRITE.equals(conf.getSyncWorkConfig().getOverMode())
					? "insert overwrite"
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
			return conf.getSyncWorkConfig().getSourceDatabase().getDbTable();
		} else {

      Properties prop = new Properties();
      prop.put("user", conf.getSyncWorkConfig().getSourceDatabase().getUser());
      prop.put("password", conf.getSyncWorkConfig().getSourceDatabase().getPassword());
      prop.put("driver", conf.getSyncWorkConfig().getSourceDatabase().getDriver());
      // 创建一个 ArrayList 存储查询条件字符串
      List<String> predicates = new ArrayList<>();

      // 生成查询条件并添加到列表中
      for (int i = 0; i < conf.getSyncRule().getNumPartitions(); i++) {
        //不同的数据库要使用各自支持hash函数
        String predicate = String.format("CRC32(`%s`) %% %d = %d", conf.getSyncWorkConfig().getPartitionColumn(), conf.getSyncRule().getNumPartitions(), i);
        predicates.add(predicate);
      }

      // 将列表转换为字符串数组
      String[] predicate = predicates.toArray(new String[0]);

      Dataset<Row> source = sparkSession.read().jdbc(conf.getSyncWorkConfig().getSourceDatabase().getUrl(),
        conf.getSyncWorkConfig().getSourceDatabase().getDbTable(),
        predicate, prop);

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
			return conf.getSyncWorkConfig().getTargetDatabase().getDbTable();
		} else {
			DataFrameReader frameReader = sparkSession.read().format("jdbc")
					.option("driver", conf.getSyncWorkConfig().getTargetDatabase().getDriver())
					.option("url", conf.getSyncWorkConfig().getTargetDatabase().getUrl())
					.option("dbtable", conf.getSyncWorkConfig().getTargetDatabase().getDbTable())
					.option("user", conf.getSyncWorkConfig().getTargetDatabase().getUser())
					.option("password", conf.getSyncWorkConfig().getTargetDatabase().getPassword())
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

		if (Strings.isEmpty(conf.get("hive.metastore.uris"))) {
			return sparkSessionBuilder.config(conf).getOrCreate();
		} else {
			return sparkSessionBuilder.config(conf).enableHiveSupport().getOrCreate();
		}
	}

}
