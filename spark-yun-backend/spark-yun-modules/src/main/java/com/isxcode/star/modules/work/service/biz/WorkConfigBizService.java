package com.isxcode.star.modules.work.service.biz;

import com.isxcode.star.api.work.pojos.req.ConfigWorkReq;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.work.entity.WorkConfigEntity;
import com.isxcode.star.modules.work.entity.WorkEntity;
import com.isxcode.star.modules.work.repository.WorkConfigRepository;
import com.isxcode.star.modules.work.repository.WorkRepository;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

/** 用户模块接口的业务逻辑. */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WorkConfigBizService {

	private final WorkRepository workRepository;

	private final WorkConfigRepository workConfigRepository;

	public WorkConfigEntity getWorkConfigEntity(String workConfigId) {

		Optional<WorkConfigEntity> workConfigEntityOptional = workConfigRepository.findById(workConfigId);
		if (!workConfigEntityOptional.isPresent()) {
			throw new IsxAppException("作业异常，请联系开发者");
		}
		return workConfigEntityOptional.get();
	}

	public void configWork(ConfigWorkReq wocConfigWorkReq) {

		Optional<WorkEntity> workEntityOptional = workRepository.findById(wocConfigWorkReq.getWorkId());
		if (!workEntityOptional.isPresent()) {
			throw new IsxAppException("作业不存在");
		}

		Optional<WorkConfigEntity> workConfigEntityOptional = workConfigRepository
				.findById(workEntityOptional.get().getConfigId());
		if (!workConfigEntityOptional.isPresent()) {
			throw new IsxAppException("作业异常，作业不可用。");
		}
		WorkConfigEntity workConfigEntity = workConfigEntityOptional.get();

		if (!Strings.isEmpty(wocConfigWorkReq.getSqlScript())) {
			workConfigEntity.setSqlScript(wocConfigWorkReq.getSqlScript());
		}
		if (!Strings.isEmpty(wocConfigWorkReq.getClusterId())) {
			workConfigEntity.setClusterId(wocConfigWorkReq.getClusterId());
		}
    if (!Strings.isEmpty(wocConfigWorkReq.getClusterNodeId())) {
      workConfigEntity.setClusterNodeId(wocConfigWorkReq.getClusterNodeId());
    }
		if (!Strings.isEmpty(wocConfigWorkReq.getDatasourceId())) {
			workConfigEntity.setDatasourceId(wocConfigWorkReq.getDatasourceId());
		}
		if (!Strings.isEmpty(wocConfigWorkReq.getSparkConfig())) {
			workConfigEntity.setSparkConfig(wocConfigWorkReq.getSparkConfig());
		}
    if (!Strings.isEmpty(wocConfigWorkReq.getBashScript())) {
      workConfigEntity.setBashScript(wocConfigWorkReq.getBashScript());
    }
		if (!Strings.isEmpty(wocConfigWorkReq.getCorn())) {
			// 检验corn表达式
			boolean validExpression = CronExpression.isValidExpression(wocConfigWorkReq.getCorn());
			if (!validExpression) {
				throw new IsxAppException("Corn表达式异常");
			}
			workConfigEntity.setCorn(wocConfigWorkReq.getCorn());
		}
		workConfigRepository.save(workConfigEntity);
	}
}
