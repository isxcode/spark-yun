package com.isxcode.star.plugin.query.sql;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.pojos.plugin.req.PluginReq;
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

    PluginReq pluginReq = parse(args);

    String regex = "/\\*(?:.|[\\n\\r])*?\\*/|--.*";
    String noCommentSql = pluginReq.getSql().replaceAll(regex, "");
    String realSql = noCommentSql.replace("\n", " ");
    String[] sqls = realSql.split(";");

    try (SparkSession sparkSession = initSparkSession(pluginReq)) {

      for (int i = 0; i < sqls.length - 1; i++) {
        if (!Strings.isEmpty(sqls[i])) {
          sparkSession.sql(sqls[i]);
        }
      }
      Dataset<Row> rowDataset = sparkSession.sql(sqls[sqls.length - 1]).limit(pluginReq.getLimit());
      exportResult(rowDataset);
    }
  }

  public static PluginReq parse(String[] args) {
    if (args.length == 0) {
      throw new RuntimeException("args is empty");
    }
    return JSON.parseObject(Base64.getDecoder().decode(args[0]), PluginReq.class);
  }

  public static SparkSession initSparkSession(PluginReq pluginReq) {

    SparkSession.Builder sparkSessionBuilder = SparkSession.builder();

    SparkConf conf = new SparkConf();
    if (pluginReq.getSparkConfig() != null) {
      for (Map.Entry<String, String> entry : pluginReq.getSparkConfig().entrySet()) {
        conf.set(entry.getKey(), entry.getValue());
      }
    }

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

    System.out.println("LogType:spark-yun\n" + JSON.toJSONString(result) + "\nEnd of LogType:spark-yun");
  }
}
