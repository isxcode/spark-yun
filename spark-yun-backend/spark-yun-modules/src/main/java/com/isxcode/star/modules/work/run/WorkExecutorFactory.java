package com.isxcode.star.modules.work.run;

import com.isxcode.star.api.work.constants.WorkType;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 执行器工厂类，返回对应作业的执行器.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class WorkExecutorFactory {

	private final ApplicationContext applicationContext;

	public WorkExecutor create(String workType) {

		switch (workType) {
			case WorkType.QUERY_SPARK_SQL :
				return applicationContext.getBean(SparkSqlExecutor.class);
			case WorkType.QUERY_JDBC_SQL :
				return applicationContext.getBean(QuerySqlExecutor.class);
			case WorkType.EXECUTE_JDBC_SQL :
				return applicationContext.getBean(ExecuteSqlExecutor.class);
			case WorkType.DATA_SYNC_JDBC :
				return applicationContext.getBean(SyncWorkExecutor.class);
			case WorkType.BASH :
				return applicationContext.getBean(BashExecutor.class);
			case WorkType.PYTHON :
				return applicationContext.getBean(PythonExecutor.class);
			case WorkType.SPARK_JAR :
				return applicationContext.getBean(SparkJarExecutor.class);
			case WorkType.SPARK_CONTAINER_SQL :
				return applicationContext.getBean(SparkContainerSqlExecutor.class);
      case WorkType.API:
        return applicationContext.getBean(ApiExecutor.class);
			case WorkType.PRQL :
				return applicationContext.getBean(PRQLExecutor.class);
			default :
				throw new IsxAppException("作业类型不存在");
		}
	}
}
