package com.isxcode.star.plugin.query.sql;

//import com.alibaba.fastjson.JSON;
//import com.isxcode.star.api.pojos.plugin.req.PluginReq;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.util.Strings;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class Execute {

  /** 最后一句sql查询. */
  public static void main(String[] args) {

    try (SparkSession sparkSession = initSparkSession()) {

      Dataset<Row> rowDataset = sparkSession.sql("show databases").limit(100);
      exportResult(rowDataset);
    }
  }

//  public static PluginReq parse(String[] args) {
//    if (args.length == 0) {
//      throw new RuntimeException("args is empty");
//    }
//    return JSON.parseObject(Base64.getDecoder().decode(args[0]), PluginReq.class);
//  }

  public static SparkSession initSparkSession() {

    SparkSession.Builder sparkSessionBuilder = SparkSession.builder();

    SparkConf conf = new SparkConf();
//    if (pluginReq.getSparkConfig() != null) {
//      for (Map.Entry<String, String> entry : pluginReq.getSparkConfig().entrySet()) {
//        conf.set(entry.getKey(), entry.getValue());
//      }
//    }

    return sparkSessionBuilder.config(conf).enableHiveSupport().getOrCreate();
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

    System.out.println("==============");
    System.out.println(result);
    System.out.println("==============");
  }
}
