package com.isxcode.star.plugin.querysql;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.pojos.agent.req.PluginReq;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class Execute {

  public static PluginReq parse(String[] args) {
    if (args.length == 0) {
      throw new RuntimeException("args is empty");
    }
    return JSON.parseObject(Base64.getDecoder().decode(args[0]), PluginReq.class);
  }

  public static SparkSession initSparkSession(PluginReq pluginReq) {

    SparkSession.Builder sparkSessionBuilder = SparkSession.builder();

    SparkConf conf = new SparkConf();
    for (Map.Entry<String, String> entry : pluginReq.getSparkConfig().entrySet()) {
      conf.set(entry.getKey(), entry.getValue());
    }

    return sparkSessionBuilder.config(conf).enableHiveSupport().getOrCreate();
  }

  public static void checkRequest(PluginReq pluginReq) {

    if (pluginReq.getSql() != null && !pluginReq.getSql().isEmpty()) {
      throw new RuntimeException("sql is empty");
    }

    if (pluginReq.getLimit() > 500) {
      throw new RuntimeException("limit must low than 500");
    }
  }

  public static void exportResult(Dataset<Row> rowDataset) {

    List<List<String>> result = new ArrayList<>();

    // 表头
    result.add(Arrays.asList(rowDataset.columns()));

    // 数据
    rowDataset
        .collectAsList()
        .forEach(
            e -> {
              List<String> metaData = new ArrayList<>();
              for (int i = 0; i < e.size(); i++) {
                metaData.add(String.valueOf(e.get(i)));
              }
              result.add(metaData);
            });

    System.out.println(JSON.toJSONString(result));
  }

  public static void main(String[] args) {

    PluginReq pluginReq = parse(args);

    SparkSession sparkSession = initSparkSession(pluginReq);

    String[] split = pluginReq.getSql().split(";");

    sparkSession.sql(split[0]);

    Dataset<Row> rowDataset = sparkSession.sql(split[1]).limit(pluginReq.getLimit());

    exportResult(rowDataset);

    sparkSession.stop();
  }
}
