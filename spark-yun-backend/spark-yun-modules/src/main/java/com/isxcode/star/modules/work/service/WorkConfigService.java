package com.isxcode.star.modules.work.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.work.constants.ResourceLevel;
import com.isxcode.star.api.work.constants.SetMode;
import com.isxcode.star.api.work.constants.WorkType;
import com.isxcode.star.api.work.pojos.dto.ClusterConfig;
import com.isxcode.star.api.work.pojos.dto.CronConfig;
import com.isxcode.star.api.work.pojos.dto.SyncRule;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.work.entity.WorkConfigEntity;
import com.isxcode.star.modules.work.repository.WorkConfigRepository;
import com.isxcode.star.modules.work.repository.WorkRepository;

import java.util.Optional;
import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户模块接口的业务逻辑.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WorkConfigService {

	private final WorkRepository workRepository;

	private final WorkConfigRepository workConfigRepository;

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
				workConfig.setScript("-- show databases");
				break;
			case WorkType.QUERY_JDBC_SQL :
				workConfig.setScript("-- show databases");
				break;
			case WorkType.EXECUTE_JDBC_SQL :
				workConfig.setScript("-- show databases");
				break;
		}
	}

	public void initClusterConfig(WorkConfigEntity workConfig) {
		workConfig.setClusterConfig(JSON.toJSONString(
				ClusterConfig.builder().setMode(SetMode.SIMPLE).resourceLevel(ResourceLevel.LOW).build()));
	}

	public void initSyncRule(WorkConfigEntity workConfig) {
		workConfig.setSyncRule(JSON
				.toJSONString(SyncRule.builder().setMode(SetMode.SIMPLE).numConcurrency(1).numPartitions(1).build()));
	}

	public void initCronConfig(WorkConfigEntity workConfig) {
		workConfig.setCronConfig(JSON.toJSONString(CronConfig.builder().enable(false).build()));
	}
}
