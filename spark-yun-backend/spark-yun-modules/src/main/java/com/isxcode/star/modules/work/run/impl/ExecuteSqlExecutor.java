package com.isxcode.star.modules.work.run.impl;

import com.isxcode.star.api.work.constants.WorkLog;
import com.isxcode.star.api.work.constants.WorkType;
import com.isxcode.star.api.work.exceptions.WorkRunException;
import com.isxcode.star.modules.alarm.service.AlarmService;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.repository.DatasourceRepository;
import com.isxcode.star.modules.datasource.service.DatasourceService;
import com.isxcode.star.modules.datasource.service.biz.DatasourceBizService;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import com.isxcode.star.modules.work.run.WorkExecutor;
import com.isxcode.star.modules.work.run.WorkRunContext;
import com.isxcode.star.modules.work.sql.SqlCommentService;
import com.isxcode.star.modules.work.sql.SqlFunctionService;
import com.isxcode.star.modules.work.sql.SqlValueService;
import com.isxcode.star.modules.workflow.repository.WorkflowInstanceRepository;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExecuteSqlExecutor extends WorkExecutor {

	private final DatasourceRepository datasourceRepository;

	private final DatasourceBizService datasourceBizService;

	private final DatasourceService datasourceService;

	private final SqlCommentService sqlCommentService;

	private final SqlValueService sqlValueService;

	private final SqlFunctionService sqlFunctionService;

	public ExecuteSqlExecutor(WorkInstanceRepository workInstanceRepository, DatasourceRepository datasourceRepository,
			WorkflowInstanceRepository workflowInstanceRepository, DatasourceBizService datasourceBizService,
			DatasourceService datasourceService, SqlCommentService sqlCommentService, SqlValueService sqlValueService,
			SqlFunctionService sqlFunctionService, AlarmService alarmService) {

		super(workInstanceRepository, workflowInstanceRepository, alarmService);
		this.datasourceRepository = datasourceRepository;
		this.datasourceBizService = datasourceBizService;
		this.datasourceService = datasourceService;
		this.sqlCommentService = sqlCommentService;
		this.sqlValueService = sqlValueService;
		this.sqlFunctionService = sqlFunctionService;
	}

	@Override
	public String getWorkType() {
		return WorkType.EXECUTE_JDBC_SQL;
	}

	@Override
	public void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance) {

		// 将线程存到Map
		WORK_THREAD.put(workInstance.getId(), Thread.currentThread());

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

		// 开始执行作业
		try (Connection connection = datasourceService.getDbConnection(datasourceEntityOptional.get());
				Statement statement = connection.createStatement()) {

			// 去掉sql中的注释
			String sqlNoComment = sqlCommentService.removeSqlComment(workRunContext.getScript());

			// 翻译sql中的系统变量
			String parseValueSql = sqlValueService.parseSqlValue(sqlNoComment);

			// 翻译sql中的系统函数
			String script = sqlFunctionService.parseSqlFunction(parseValueSql);

			// 清除脚本中的脏数据
			List<String> sqls = Arrays.stream(script.split(";")).filter(e -> !Strings.isEmpty(e))
					.collect(Collectors.toList());

			// 逐条执行sql
			for (String sql : sqls) {

				// 记录开始执行时间
				logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行SQL: \n").append(sql)
						.append(" \n");
				workInstance = updateInstance(workInstance, logBuilder);

				// 执行sql
				statement.execute(sql);

				// 记录结束执行时间
				logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("SQL执行成功  \n");
				workInstance = updateInstance(workInstance, logBuilder);
			}
		} catch (WorkRunException e) {
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + e.getMsg() + "\n");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + e.getMessage() + "\n");
		}
	}

	@Override
	protected void abort(WorkInstanceEntity workInstance) {

		Thread thread = WORK_THREAD.get(workInstance.getId());
		thread.interrupt();
	}
}
