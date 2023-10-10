package com.isxcode.star.plugin.kafka.to.jdbc;

public class KafkaRow implements java.io.Serializable {
  private String record;

  public String getRecord() {
    return record;
  }

  public void setRecord(String record) {
    this.record = record;
  }
}
