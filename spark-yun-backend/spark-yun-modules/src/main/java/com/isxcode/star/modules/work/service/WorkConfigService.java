package com.isxcode.star.modules.work.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.api.work.constants.ResourceLevel;
import com.isxcode.star.api.work.constants.SetMode;
import com.isxcode.star.api.work.constants.WorkType;
import com.isxcode.star.api.work.pojos.dto.ClusterConfig;
import com.isxcode.star.api.work.pojos.dto.CronConfig;
import com.isxcode.star.api.work.pojos.dto.SyncRule;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.service.DatasourceService;
import com.isxcode.star.modules.work.entity.WorkConfigEntity;
import com.isxcode.star.modules.work.repository.WorkConfigRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 用户模块接口的业务逻辑.
 */
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
			case WorkType.QUERY_SPARK_SQL :
			case WorkType.QUERY_JDBC_SQL :
			case WorkType.EXECUTE_JDBC_SQL :
			case WorkType.SPARK_CONTAINER_SQL :
        workConfig.setScript(datasourceService.genDefaultSql(workConfig.getDatasourceId()));
				break;
			case WorkType.BASH :
				workConfig.setScript("#!/bin/bash \n" + "\n" + "pwd");
				break;
			case WorkType.PYTHON :
				workConfig.setScript("print('hello world')");
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

	public void initSyncRule(WorkConfigEntity workConfig) {
		workConfig.setSyncRule(JSON
				.toJSONString(SyncRule.builder().setMode(SetMode.SIMPLE).numConcurrency(1).numPartitions(1).build()));
	}

	public void initCronConfig(WorkConfigEntity workConfig) {
		workConfig.setCronConfig(
				JSON.toJSONString(CronConfig.builder().setMode(SetMode.SIMPLE).type("ALL").enable(false).build()));
	}

	public Map<String, String> initSparkConfig(String resourceLevel) {

		Map<String, String> sparkConfig = new HashMap<>();
		switch (resourceLevel) {
			case ResourceLevel.HIGH :
				sparkConfig.put("spark.executor.instances", "10");
				sparkConfig.put("spark.executor.cores", "4");
				sparkConfig.put("spark.executor.memory", "4g");
				sparkConfig.put("spark.driver.memory", "2g");
				sparkConfig.put("spark.driver.cores", "1");
				sparkConfig.put("spark.cores.max", "10");
				sparkConfig.put("spark.driver.extraJavaOptions", "-Dfile.encoding=utf-8");
				sparkConfig.put("spark.executor.extraJavaOptions", "-Dfile.encoding=utf-8");
				sparkConfig.put("spark.sql.storeAssignmentPolicy", "LEGACY");
				sparkConfig.put("spark.sql.legacy.timeParserPolicy", "LEGACY");
				break;
			case ResourceLevel.MEDIUM :
				sparkConfig.put("spark.executor.instances", "5");
				sparkConfig.put("spark.executor.cores", "2");
				sparkConfig.put("spark.executor.memory", "2g");
				sparkConfig.put("spark.driver.memory", "1g");
				sparkConfig.put("spark.driver.cores", "1");
				sparkConfig.put("spark.cores.max", "5");
				sparkConfig.put("spark.driver.extraJavaOptions", "-Dfile.encoding=utf-8");
				sparkConfig.put("spark.executor.extraJavaOptions", "-Dfile.encoding=utf-8");
				sparkConfig.put("spark.sql.storeAssignmentPolicy", "LEGACY");
				sparkConfig.put("spark.sql.legacy.timeParserPolicy", "LEGACY");
				break;
			case ResourceLevel.LOW :
				sparkConfig.put("spark.executor.instances", "1");
				sparkConfig.put("spark.executor.cores", "1");
				sparkConfig.put("spark.executor.memory", "2g");
				sparkConfig.put("spark.driver.memory", "1g");
				sparkConfig.put("spark.driver.cores", "1");
				sparkConfig.put("spark.cores.max", "1");
				sparkConfig.put("spark.driver.extraJavaOptions", "-Dfile.encoding=utf-8");
				sparkConfig.put("spark.executor.extraJavaOptions", "-Dfile.encoding=utf-8");
				sparkConfig.put("spark.sql.storeAssignmentPolicy", "LEGACY");
				sparkConfig.put("spark.sql.legacy.timeParserPolicy", "LEGACY");
				break;
		}
		return sparkConfig;
	}
}
