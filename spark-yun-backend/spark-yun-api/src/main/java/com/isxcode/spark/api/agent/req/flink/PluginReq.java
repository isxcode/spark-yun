package com.isxcode.spark.api.agent.req.flink;

import java.util.List;
import java.util.Map;

import com.isxcode.spark.api.datasource.dto.KafkaConfig;
import com.isxcode.spark.api.func.dto.FuncInfo;
import com.isxcode.spark.api.work.dto.ExcelSyncConfig;
import com.isxcode.spark.api.work.dto.SyncRule;
import com.isxcode.spark.api.work.dto.SyncWorkConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PluginReq {

    private String sql;

    private String database;

    private Integer limit;

    private String applicationId;

    private Map<String, String> flinkConfig;

    private SyncWorkConfig syncWorkConfig;

    private ExcelSyncConfig excelSyncConfig;

    private SyncRule syncRule;

    private List<FuncInfo> funcInfoList;

    private int containerPort;

    private KafkaConfig kafkaConfig;

    private String csvFilePath;

    private String csvFileName;

    private String agentType;

    private String[] args;
}
