package com.isxcode.star.modules.work.run;

import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 执行器工厂类，返回对应作业的执行器.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class WorkExecutorFactory {

	private final ApplicationContext applicationContext;

	public WorkExecutor create(String workType) {

		Optional<WorkExecutor> workExecutorOptional = applicationContext.getBeansOfType(WorkExecutor.class).values()
				.stream().filter(agent -> agent.getWorkType().equals(workType)).findFirst();

		if (!workExecutorOptional.isPresent()) {
			throw new IsxAppException("作业类型不支持");
		}

		return workExecutorOptional.get();
	}
}
