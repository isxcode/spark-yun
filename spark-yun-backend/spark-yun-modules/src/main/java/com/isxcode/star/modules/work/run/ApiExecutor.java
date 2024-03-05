package com.isxcode.star.modules.work.run;

import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import com.isxcode.star.modules.workflow.repository.WorkflowInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ApiExecutor extends WorkExecutor {

	private final WorkInstanceRepository workInstanceRepository;

	public ApiExecutor(WorkInstanceRepository workInstanceRepository,
			WorkflowInstanceRepository workflowInstanceRepository) {

		super(workInstanceRepository, workflowInstanceRepository);
		this.workInstanceRepository = workInstanceRepository;
	}

	@Override
	protected void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance) {

	}

	@Override
	protected void abort(WorkInstanceEntity workInstance) {

	}
}
