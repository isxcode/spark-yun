package com.isxcode.star.modules.work.service.biz;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.isxcode.star.api.work.constants.SetMode;
import com.isxcode.star.api.work.pojos.req.ConfigWorkReq;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.repository.DatasourceRepository;
import com.isxcode.star.modules.work.entity.WorkConfigEntity;
import com.isxcode.star.modules.work.entity.WorkEntity;
import com.isxcode.star.modules.work.repository.WorkConfigRepository;

import java.util.Map;
import java.util.Optional;
import javax.transaction.Transactional;

import com.isxcode.star.modules.work.service.WorkConfigService;
import com.isxcode.star.modules.work.service.WorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

/**
 * 用户模块接口的业务逻辑.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WorkConfigBizService {

	private final WorkService workService;

	private final WorkConfigService workConfigService;

	private final WorkConfigRepository workConfigRepository;

	private final DatasourceRepository datasourceRepository;

	public WorkConfigEntity getWorkConfigEntity(String workConfigId) {

		Optional<WorkConfigEntity> workConfigEntityOptional = workConfigRepository.findById(workConfigId);
		if (!workConfigEntityOptional.isPresent()) {
			throw new IsxAppException("作业异常，请联系开发者");
		}
		return workConfigEntityOptional.get();
	}

	public void configWork(ConfigWorkReq wocConfigWorkReq) {

		// 先转换sparkConfigJson和sqlConfigJson
		try {
			if (wocConfigWorkReq.getClusterConfig() != null
					&& !Strings.isEmpty(wocConfigWorkReq.getClusterConfig().getSparkConfigJson())) {
				wocConfigWorkReq.getClusterConfig()
						.setSparkConfig(JSON.parseObject(wocConfigWorkReq.getClusterConfig().getSparkConfigJson(),
								new TypeReference<Map<String, String>>() {
								}));
			}
		} catch (Exception e) {
			throw new IsxAppException("集群spark配置json格式不合法");
		}

		try {
			if (wocConfigWorkReq.getSyncRule() != null
					&& !Strings.isEmpty(wocConfigWorkReq.getSyncRule().getSqlConfigJson())) {
				wocConfigWorkReq.getSyncRule().setSqlConfig(JSON.parseObject(
						wocConfigWorkReq.getSyncRule().getSqlConfigJson(), new TypeReference<Map<String, String>>() {
						}));
			}
		} catch (Exception e) {
			throw new IsxAppException("数据同步sqlConfig配置json格式不合法");
		}

		WorkEntity work = workService.getWorkEntity(wocConfigWorkReq.getWorkId());

		WorkConfigEntity workConfig = workConfigService.getWorkConfigEntity(work.getConfigId());

		// 用户更新脚本
		if (!Strings.isEmpty(wocConfigWorkReq.getScript())) {
			workConfig.setScript(wocConfigWorkReq.getScript());
		}

		// 用户更新集群配置
		if (wocConfigWorkReq.getClusterConfig() != null) {

			// 如果spark开启了hive数据源，要保存hiveStoreUrl配置
			String hiveMetaStoreUris = null;
			if (wocConfigWorkReq.getClusterConfig().getEnableHive() != null
					&& wocConfigWorkReq.getClusterConfig().getEnableHive()) {
				Optional<DatasourceEntity> datasourceEntity = datasourceRepository
						.findById(workConfig.getDatasourceId());
				if (datasourceEntity.isPresent()) {
					hiveMetaStoreUris = datasourceEntity.get().getMetastoreUris();
				}
			}

			// 如果是等级的模式，需要帮用户默认填充sparkConfig
			if (SetMode.SIMPLE.equals(wocConfigWorkReq.getClusterConfig().getSetMode())) {
				workConfigService.initSparkConfig(wocConfigWorkReq.getClusterConfig().getResourceLevel(),
						hiveMetaStoreUris);
			}
			workConfig.setClusterConfig(JSON.toJSONString(wocConfigWorkReq.getClusterConfig()));
		}

		// 用户更新数据源
		if (!Strings.isEmpty(wocConfigWorkReq.getDatasourceId())) {
			workConfig.setDatasourceId(wocConfigWorkReq.getDatasourceId());
		}

		// 用户更新数据同步
		if (wocConfigWorkReq.getSyncWorkConfig() != null) {
			workConfig.setSyncWorkConfig(JSON.toJSONString(wocConfigWorkReq.getSyncWorkConfig()));
		}

		// 用户更新数据同步规则
		if (wocConfigWorkReq.getSyncRule() != null) {
			workConfig.setSyncRule(JSON.toJSONString(wocConfigWorkReq.getSyncRule()));
		}

		// 用户更新调度配置
		if (wocConfigWorkReq.getCronConfig() != null) {
			if (SetMode.ADVANCE.equals(wocConfigWorkReq.getCronConfig().getSetMode())) {
				boolean validExpression = CronExpression.isValidExpression(wocConfigWorkReq.getCronConfig().getCron());
				if (!validExpression) {
					throw new IsxAppException("Corn表达式异常");
				}
			}
			workConfig.setCronConfig(JSON.toJSONString(wocConfigWorkReq.getCronConfig()));
		}

		// 保存配置
		workConfigRepository.save(workConfig);
	}
}
