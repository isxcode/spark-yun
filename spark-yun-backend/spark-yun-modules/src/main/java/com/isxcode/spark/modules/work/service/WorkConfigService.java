package com.isxcode.spark.modules.work.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.spark.api.work.constants.ResourceLevel;
import com.isxcode.spark.api.work.constants.SetMode;
import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.api.work.dto.ClusterConfig;
import com.isxcode.spark.api.work.dto.CronConfig;
import com.isxcode.spark.api.work.dto.SyncRule;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.modules.datasource.entity.DatasourceEntity;
import com.isxcode.spark.modules.datasource.service.DatasourceService;
import com.isxcode.spark.modules.work.entity.WorkConfigEntity;
import com.isxcode.spark.modules.work.repository.WorkConfigRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WorkConfigService {

    private final WorkConfigRepository workConfigRepository;

    private final DatasourceService datasourceService;

    public WorkConfigEntity getWorkConfigEntity(String workConfigId) {

        Optional<WorkConfigEntity> workConfigEntityOptional = workConfigRepository.findById(workConfigId);
        if (!workConfigEntityOptional.isPresent()) {
            throw new IsxAppException("作业异常，请联系开发者");
        }
        return workConfigEntityOptional.get();
    }

    public void initWorkScript(WorkConfigEntity workConfig, String workType) {

        switch (workType) {
            case WorkType.QUERY_SPARK_SQL:
            case WorkType.QUERY_JDBC_SQL:
            case WorkType.EXECUTE_JDBC_SQL:
            case WorkType.SPARK_CONTAINER_SQL:
                workConfig.setScript("show tables");
                break;
            case WorkType.FLINK_SQL:
                workConfig.setScript("CREATE TABLE print_sink ( \n" + "    print_date timestamp \n" + ") WITH ( \n"
                    + "    'connector' = 'print' \n" + ");\n" + "\n" + "INSERT INTO print_sink SELECT now();");
                break;
            case WorkType.BASH:
                workConfig.setScript("#!/bin/bash \n" + "\n set -euo pipefail \n pwd");
                break;
            case WorkType.PYTHON:
                workConfig.setScript("print('hello world')");
                break;
            case WorkType.PRQL:
                workConfig.setScript("from table_name");
                break;
            case WorkType.PY_SPARK:
                workConfig.setScript(
                    "from pyspark.sql import SparkSession\n" + "\n" + "spark = SparkSession.builder.getOrCreate()\n"
                        + "da = [('zhangsan', 13), \n" + "      ('lisi', 14),]\n" + "col = ['username', 'age']\n"
                        + "stu = spark.createDataFrame(data=da, schema=col)\n" + "stu.show()");
                break;
        }
    }

    public void initClusterConfig(WorkConfigEntity workConfig, String clusterId, String clusterNodeId,
        Boolean enableHive, String datasourceId) {

        Map<String, String> sparkConfig = initSparkConfig(ResourceLevel.LOW);
        if (enableHive) {
            DatasourceEntity datasource = datasourceService.getDatasource(datasourceId);
            sparkConfig.put("hive.metastore.uris", datasource.getMetastoreUris());
        }

        workConfig.setClusterConfig(JSON.toJSONString(
            ClusterConfig.builder().setMode(SetMode.SIMPLE).clusterId(clusterId).clusterNodeId(clusterNodeId)
                .enableHive(enableHive).sparkConfig(sparkConfig).resourceLevel(ResourceLevel.LOW).build()));
    }

    public void initFlinkClusterConfig(WorkConfigEntity workConfig, String clusterId, String clusterNodeId,
        Boolean enableHive, String datasourceId) {

        Map<String, Object> flinkConfig = initFlinkConfig(ResourceLevel.LOW);
        workConfig.setClusterConfig(JSON.toJSONString(
            ClusterConfig.builder().setMode(SetMode.SIMPLE).clusterId(clusterId).clusterNodeId(clusterNodeId)
                .enableHive(enableHive).flinkConfig(flinkConfig).resourceLevel(ResourceLevel.LOW).build()));
    }

    public void initSyncRule(WorkConfigEntity workConfig) {
        workConfig.setSyncRule(
            JSON.toJSONString(SyncRule.builder().setMode(SetMode.SIMPLE).numConcurrency(1).numPartitions(1).build()));
    }

    public void initCronConfig(WorkConfigEntity workConfig) {
        workConfig.setCronConfig(
            JSON.toJSONString(CronConfig.builder().setMode(SetMode.SIMPLE).type("ALL").enable(false).build()));
    }

    public Map<String, String> initSparkConfig(String resourceLevel) {

        Map<String, String> sparkConfig = new HashMap<>();
        switch (resourceLevel) {
            case ResourceLevel.HIGH:
                sparkConfig.put("spark.driver.cores", "1");
                sparkConfig.put("spark.driver.memory", "2g");
                sparkConfig.put("spark.executor.cores", "1");
                sparkConfig.put("spark.executor.memory", "8g");
                sparkConfig.put("spark.executor.instances", "3");
                sparkConfig.put("spark.cores.max", "3");
                break;
            case ResourceLevel.MEDIUM:
                sparkConfig.put("spark.driver.cores", "1");
                sparkConfig.put("spark.driver.memory", "1g");
                sparkConfig.put("spark.executor.cores", "1");
                sparkConfig.put("spark.executor.memory", "4g");
                sparkConfig.put("spark.executor.instances", "2");
                sparkConfig.put("spark.cores.max", "2");
                break;
            case ResourceLevel.LOW:
                sparkConfig.put("spark.driver.cores", "1");
                sparkConfig.put("spark.driver.memory", "1g");
                sparkConfig.put("spark.executor.cores", "1");
                sparkConfig.put("spark.executor.memory", "2g");
                sparkConfig.put("spark.executor.instances", "1");
                sparkConfig.put("spark.cores.max", "1");
                break;
        }

        sparkConfig.put("spark.driver.extraJavaOptions", "-Dfile.encoding=utf-8");
        sparkConfig.put("spark.executor.extraJavaOptions", "-Dfile.encoding=utf-8");
        sparkConfig.put("spark.sql.storeAssignmentPolicy", "LEGACY");
        sparkConfig.put("spark.sql.legacy.timeParserPolicy", "LEGACY");
        sparkConfig.put("spark.sql.autoBroadcastJoinThreshold", "-1");
        sparkConfig.put("spark.sql.parquet.writeLegacyFormat", "true");
        sparkConfig.put("spark.sql.parquet.enableVectorizedReader", "false");
        sparkConfig.put("spark.sql.parquet.int96RebaseModeInRead", "LEGACY");
        sparkConfig.put("spark.sql.parquet.int96RebaseModeInWrite", "LEGACY");
        sparkConfig.put("spark.sql.parquet.datetimeRebaseModeInRead", "LEGACY");
        sparkConfig.put("spark.sql.parquet.datetimeRebaseModeInWrite", "LEGACY");
        return sparkConfig;
    }

    public Map<String, Object> initFlinkConfig(String resourceLevel) {

        Map<String, Object> flinkConfig = new HashMap<>();
        switch (resourceLevel) {
            case ResourceLevel.HIGH:
                flinkConfig.put("jobmanager.memory.process.size", "2g");
                flinkConfig.put("taskmanager.memory.process.size", "8g");
                flinkConfig.put("taskmanager.numberOfTaskSlots", 3);
                flinkConfig.put("parallelism.default", 3);
                break;
            case ResourceLevel.MEDIUM:
                flinkConfig.put("jobmanager.memory.process.size", "1g");
                flinkConfig.put("taskmanager.memory.process.size", "4g");
                flinkConfig.put("taskmanager.numberOfTaskSlots", 2);
                flinkConfig.put("parallelism.default", 2);
                break;
            case ResourceLevel.LOW:
                flinkConfig.put("jobmanager.memory.process.size", "1g");
                flinkConfig.put("taskmanager.memory.process.size", "2g");
                flinkConfig.put("taskmanager.numberOfTaskSlots", 1);
                flinkConfig.put("parallelism.default", 1);
                break;
        }

        return flinkConfig;
    }
}
