package com.isxcode.star.modules.work.run;

import com.isxcode.star.api.work.constants.WorkLog;
import com.isxcode.star.api.work.exceptions.WorkRunException;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.repository.DatasourceRepository;
import com.isxcode.star.modules.datasource.service.DatasourceService;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import com.isxcode.star.modules.workflow.repository.WorkflowInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class PRQLExecutor extends WorkExecutor {

	private final DatasourceRepository datasourceRepository;
	private final DatasourceService datasourceService;

	public PRQLExecutor(WorkInstanceRepository workInstanceRepository,
			WorkflowInstanceRepository workflowInstanceRepository, DatasourceRepository datasourceRepository,
			DatasourceService datasourceService) {
		super(workInstanceRepository, workflowInstanceRepository);
		this.datasourceRepository = datasourceRepository;
		this.datasourceService = datasourceService;
	}

	@Override
	protected void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance) {

		// 获取日志构造器
		StringBuilder logBuilder = workRunContext.getLogBuilder();

		// 检测数据源是否配置
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始检测运行环境 \n");
		if (Strings.isEmpty(workRunContext.getDatasourceId())) {
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测运行环境失败: 未配置有效数据源  \n");
		}

		// 检查数据源是否存在
		Optional<DatasourceEntity> datasourceEntityOptional = datasourceRepository
				.findById(workRunContext.getDatasourceId());
		if (!datasourceEntityOptional.isPresent()) {
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测运行环境失败: 未配置有效数据源  \n");
		}

		// 数据源检查通过
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测运行环境完成  \n");
		workInstance = updateInstance(workInstance, logBuilder);

		// 检查脚本是否为空
		if (Strings.isEmpty(workRunContext.getScript())) {
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "Sql内容为空 \n");
		}

		// 脚本检查通过
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行作业 \n");
		workInstance = updateInstance(workInstance, logBuilder);

		// 开始执行sql
		try (Connection connection = datasourceService.getDbConnection(datasourceEntityOptional.get());
				Statement statement = connection.createStatement()) {

			statement.setQueryTimeout(1800);

			// 解析sql

		} catch (Exception e) {

			log.error(e.getMessage(), e);
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + e.getMessage() + "\n");
		}

	}

	@Override
	protected void abort(WorkInstanceEntity workInstance) {

	}
}
