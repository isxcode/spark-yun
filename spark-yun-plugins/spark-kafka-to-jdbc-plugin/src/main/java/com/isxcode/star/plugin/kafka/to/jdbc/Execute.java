package com.isxcode.star.plugin.kafka.to.jdbc;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.agent.pojos.req.PluginReq;

import java.util.*;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.util.Strings;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.api.java.JavaStreamingContext$;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

public class Execute {

  public static void main(String[] args) {

    PluginReq pluginReq = parse(args);

    try (SparkSession sparkSession = initSparkSession(pluginReq)) {

      Map<String, Object> kafkaConfig = pluginReq.getKafkaConfig();
      kafkaConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
      kafkaConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

      SparkConf conf = new SparkConf();
      for (Map.Entry<String, String> entry : pluginReq.getSparkConfig().entrySet()) {
        conf.set(entry.getKey(), entry.getValue());
      }

      try (JavaStreamingContext javaStreamingContext = new JavaStreamingContext(conf, new Duration(1000))) {

        HashSet<String> topics = new HashSet<>();
        topics.add(String.valueOf(kafkaConfig.get("topic")));

        JavaDStream<ConsumerRecord<String, String>> directStream = KafkaUtils.createDirectStream(
          javaStreamingContext,
          LocationStrategies.PreferConsistent(),
          ConsumerStrategies.Subscribe(topics, kafkaConfig));

        List<String> columns = Arrays.asList(String.valueOf(kafkaConfig.get("columns")).split(","));

        directStream
          .filter(d -> {
            String[] split = d.value().split(",");
            return split.length >= columns.size();
          }).map(rdd -> {
            KafkaRow record = new KafkaRow();
            record.setRecord(rdd.value());
            return record;
          }).foreachRDD(e -> {
            if (e.count() > 0) {
              Dataset<Row> dataFrame = sparkSession.createDataFrame(e, KafkaRow.class);
              for (String column : columns) {
                UDF1<String, String> SplitRecord = (record) -> record.split(",")[columns.indexOf(column)];
                sparkSession.udf().register("SplitRecord", SplitRecord, DataTypes.StringType);
                dataFrame = dataFrame.withColumn(column, functions.callUDF("SplitRecord", dataFrame.col("record")));
              }
              dataFrame = dataFrame.drop(dataFrame.col("record"));
              dataFrame.createOrReplaceTempView(String.valueOf(kafkaConfig.get("name")));
              hiveContext.sql(pluginReq.getSql());
            }
          });
        javaStreamingContext.start();
        javaStreamingContext.awaitTermination();
      } catch (InterruptedException e) {
          throw new RuntimeException(e);
      }
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

    if (pluginReq.getSparkConfig() != null
      && Strings.isEmpty(pluginReq.getSparkConfig().get("hive.metastore.uris"))) {
      return sparkSessionBuilder.config(conf).getOrCreate();
    } else {
      return sparkSessionBuilder.config(conf).enableHiveSupport().getOrCreate();
    }
  }

  public static void exportResult(Dataset<Row> rowDataset) {

    List<List<String>> result = new ArrayList<>();

    // 表头
    result.add(Arrays.asList(rowDataset.columns()));

    // 数据
    rowDataset.collectAsList().forEach(e -> {
      List<String> metaData = new ArrayList<>();
      for (int i = 0; i < e.size(); i++) {
        metaData.add(String.valueOf(e.get(i)));
      }
      result.add(metaData);
    });

    System.out.println("LogType:spark-yun\n" + JSON.toJSONString(result) + "\nEnd of LogType:spark-yun");
  }
}
