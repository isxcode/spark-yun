package com.isxcode.star.plugin.querysql;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class Execute {

  public static void main(String[] args) {

    SparkSession.Builder sparkSessionBuilder = SparkSession.builder();

    SparkConf conf = new SparkConf();
    //    for (Map.Entry<String, String> entry : pluginReq.getSparkConfig().entrySet()) {
    //      conf.set(entry.getKey(), entry.getValue());
    //    }

    SparkSession sparkSession = sparkSessionBuilder.config(conf).enableHiveSupport().getOrCreate();

    Dataset<Row> showDatabases = sparkSession.sql("show databases");
    showDatabases.foreach(
        e -> {
          System.out.println(e.getString(0));
        });

    sparkSession.stop();
  }
}
