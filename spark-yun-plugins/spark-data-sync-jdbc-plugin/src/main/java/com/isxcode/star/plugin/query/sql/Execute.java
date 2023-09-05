package com.isxcode.star.plugin.query.sql;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.agent.pojos.req.JDBCSyncPluginReq;
import org.apache.logging.log4j.util.Strings;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.Base64;
import java.util.Map;

public class Execute {

	/** 最后一句sql查询. */
	public static void main(String[] args) {

    JDBCSyncPluginReq conf = parse(args);

    try (SparkSession sparkSession = initSparkSession(conf.getSparkConfig())) {
      // 读取源数据并创建临时表
      Dataset<Row> source = sparkSession.read()
        .format("jdbc")
        .option("url", conf.getSourceDbInfo().getUrl())
        .option("dbtable", conf.getSourceDbInfo().getTableName())
        .option("user", conf.getSourceDbInfo().getUser())
        .option("password", conf.getSourceDbInfo().getPassword())
        .load();
      source.createOrReplaceTempView("source_temp_table");

      // 读取目标数据并创建临时表
      Dataset<Row> target = sparkSession.read().format("jdbc")
        .option("url", conf.getTargetDbInfo().getUrl())
        .option("dbtable", conf.getTargetDbInfo().getTableName())
        .option("user", conf.getTargetDbInfo().getUser())
        .option("password", conf.getTargetDbInfo().getPassword())
        .option("truncate", "true")
        .load();
      target.createOrReplaceTempView("target_temp_table");
      String sourceColums =  "";
      for (String column : target.columns()) {
        if (null == conf.getColumMapping().get(column)) {
          sourceColums += "null" + ",";
        }else {
          String sourceColum = conf.getColumMapping().get(column).get(1) != null && !"".equals(conf.getColumMapping().get(column).get(1)) ?
            conf.getColumMapping().get(column).get(1) : conf.getColumMapping().get(column).get(0);
          //含有"'"则表示希望写入固定值
          if(sourceColum.contains("'")){
            sourceColums += sourceColum + ",";
          }else {
            sourceColums += '`'+ sourceColum + '`' + ",";
          }
        }

      }

      String sql = "overwrite".equals(conf.getOverMode()) ? "INSERT OVERWRITE target_temp_table ":"INSERT INTO target_temp_table ";

      sourceColums = sourceColums.substring(0, sourceColums.length() - 1);

      sql = sql + " SELECT " + sourceColums + " FROM source_temp_table " + conf.getCondition();

      // 执行数据同步操作
      sparkSession.sql(sql);

      System.out.println("LogType:spark-yun\n同步成功\nEnd of LogType:spark-yun");

    }
	}

	public static JDBCSyncPluginReq parse(String[] args) {
		if (args.length == 0) {
			throw new RuntimeException("args is empty");
		}
		return JSON.parseObject(Base64.getDecoder().decode(args[0]), JDBCSyncPluginReq.class);
	}

	public static SparkSession initSparkSession(Map<String, String> sparkConfig) {

		SparkSession.Builder sparkSessionBuilder = SparkSession.builder();

		SparkConf conf = new SparkConf();
		if (sparkConfig != null) {
			for (Map.Entry<String, String> entry : sparkConfig.entrySet()) {
				conf.set(entry.getKey(), entry.getValue());
			}
		}

		if (sparkConfig != null
				&& Strings.isEmpty(sparkConfig.get("hive.metastore.uris"))) {
			return sparkSessionBuilder.config(conf).getOrCreate();
		} else {
			return sparkSessionBuilder.config(conf).enableHiveSupport().getOrCreate();
		}
	}

}
