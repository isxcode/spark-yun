package com.isxcode.star.modules.work.service.biz;

import com.alibaba.fastjson2.JSON;
import com.isxcode.star.api.work.constants.SetMode;
import com.isxcode.star.api.work.constants.WorkType;
import com.isxcode.star.api.work.pojos.req.ConfigWorkReq;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.work.entity.WorkConfigEntity;
import com.isxcode.star.modules.work.entity.WorkEntity;
import com.isxcode.star.modules.work.repository.WorkConfigRepository;

import java.util.Optional;
import javax.transaction.Transactional;

import com.isxcode.star.modules.work.service.WorkConfigService;
import com.isxcode.star.modules.work.service.WorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

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

	public WorkConfigEntity getWorkConfigEntity(String workConfigId) {

		Optional<WorkConfigEntity> workConfigEntityOptional = workConfigRepository.findById(workConfigId);
		if (!workConfigEntityOptional.isPresent()) {
			throw new IsxAppException("作业异常，请联系开发者");
		}
		return workConfigEntityOptional.get();
	}

	public void configWork(ConfigWorkReq wocConfigWorkReq) {

		WorkEntity work = workService.getWorkEntity(wocConfigWorkReq.getWorkId());

		// 如果作业是spark，且是高级模式，检查用户的json格式
		if (WorkType.QUERY_SPARK_SQL.equals(work.getWorkType())
				&& SetMode.ADVANCE.equals(wocConfigWorkReq.getClusterConfig().getSetMode())
				&& !Strings.isEmpty(wocConfigWorkReq.getClusterConfig().getSparkConfig())
				&& !JSON.isValid(wocConfigWorkReq.getClusterConfig().getSparkConfig())) {
			throw new IsxAppException("500", "sparkConfig中json格式不合法");
		}

		WorkConfigEntity workConfig = workConfigService.getWorkConfigEntity(work.getConfigId());

		// 用户更新脚本
		if (!Strings.isEmpty(wocConfigWorkReq.getScript())) {
			workConfig.setScript(wocConfigWorkReq.getScript());
		}

		// 用户更新集群配置
		if (wocConfigWorkReq.getClusterConfig() != null) {
			workConfig.setClusterConfig(JSON.toJSONString(wocConfigWorkReq.getClusterConfig()));
		}

		// 用户更新数据源
		if (!Strings.isEmpty(wocConfigWorkReq.getDatasourceId())) {
			workConfig.setClusterConfig(wocConfigWorkReq.getDatasourceId());
		}

		// 用户更新数据同步
		if (wocConfigWorkReq.getSyncWorkConfig() != null) {
			workConfig.setSyncWorkConfig(JSON.toJSONString(wocConfigWorkReq.getSyncWorkConfig()));
		}

		// 用户更新数据同步规则
		if (wocConfigWorkReq.getSyncRule() != null) {
			workConfig.setSyncWorkConfig(JSON.toJSONString(wocConfigWorkReq.getSyncRule()));
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
