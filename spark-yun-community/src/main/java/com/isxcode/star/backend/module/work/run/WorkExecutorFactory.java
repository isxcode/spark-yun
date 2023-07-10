package com.isxcode.star.backend.module.work.run;

import com.isxcode.star.api.constants.work.WorkType;
import com.isxcode.star.api.exceptions.SparkYunException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/** 执行器工厂类，返回对应作业的执行器. */
@Slf4j
@RequiredArgsConstructor
@Component
public class WorkExecutorFactory {

  private final ApplicationContext applicationContext;

  public WorkExecutor create(String workType) {

    switch (workType) {
      case WorkType.QUERY_SPARK_SQL:
        return applicationContext.getBean(SparkSqlExecutor.class);
      case WorkType.QUERY_JDBC_SQL:
        return applicationContext.getBean(QuerySqlExecutor.class);
      case WorkType.EXECUTE_JDBC_SQL:
        return applicationContext.getBean(ExecuteSqlExecutor.class);
      default:
        throw new SparkYunException("作业类型不存在");
    }
  }
}
