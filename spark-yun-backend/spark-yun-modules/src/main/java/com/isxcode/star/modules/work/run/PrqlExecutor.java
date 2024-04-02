package com.isxcode.star.modules.work.run;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.api.work.constants.WorkLog;
import com.isxcode.star.api.work.exceptions.WorkRunException;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.repository.DatasourceRepository;
import com.isxcode.star.modules.datasource.service.DatasourceService;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import com.isxcode.star.modules.workflow.repository.WorkflowInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.prql.prql4j.PrqlCompiler;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PrqlExecutor extends WorkExecutor {

	private final DatasourceRepository datasourceRepository;
	private final DatasourceService datasourceService;

	public PrqlExecutor(WorkInstanceRepository workInstanceRepository,
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

			logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始解析prql \n");
			workInstance = updateInstance(workInstance, logBuilder);
			// 解析sql
			String sql = PrqlCompiler.toSql(workRunContext.getScript(),
					translateDBType(datasourceEntityOptional.get().getDbType()), true, true);

			String regex = "/\\*(?:.|[\\n\\r])*?\\*/|--.*";
			String noCommentSql = sql.replaceAll(regex, "");
			String realSql = noCommentSql.replaceAll("--.*", "").replace("\n", " ");

			logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO)
					.append(String.format("prql转化完成: \n%s\n", realSql));
			workInstance = updateInstance(workInstance, logBuilder);

			logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行SQL \n");
			workInstance = updateInstance(workInstance, logBuilder);
			statement.execute(realSql);

			// 记录结束执行时间
			logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("SQL执行成功  \n");
			workInstance = updateInstance(workInstance, logBuilder);

			ResultSet resultSet = statement.getResultSet();

			// 记录返回结果
			List<List<String>> result = new ArrayList<>();

			// 封装表头
			int columnCount = resultSet.getMetaData().getColumnCount();
			List<String> metaList = new ArrayList<>();
			for (int i = 1; i <= columnCount; i++) {
				metaList.add(resultSet.getMetaData().getColumnName(i));
			}
			result.add(metaList);

			// 封装数据
			while (resultSet.next()) {
				metaList = new ArrayList<>();
				for (int i = 1; i <= columnCount; i++) {
					metaList.add(String.valueOf(resultSet.getObject(i)));
				}
				result.add(metaList);
			}

			// 讲data转为json存到实例中
			logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("数据保存成功  \n");
			workInstance.setResultData(JSON.toJSONString(result));
			updateInstance(workInstance, logBuilder);
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

	public static String translateDBType(String dbType) {

		switch (dbType) {
			case DatasourceType.MYSQL :
				return "mysql";
			case DatasourceType.CLICKHOUSE :
				return "clickhouse";
			case DatasourceType.POSTGRE_SQL :
				return "postgres";
			default :
				throw new IsxAppException("当前数据库类型不支持");
		}
	}
}
