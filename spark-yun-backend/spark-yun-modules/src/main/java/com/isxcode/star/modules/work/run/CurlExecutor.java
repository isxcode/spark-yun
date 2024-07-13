package com.isxcode.star.modules.work.run;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import com.isxcode.star.api.work.constants.WorkLog;
import com.isxcode.star.api.work.exceptions.WorkRunException;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import com.isxcode.star.common.utils.path.PathUtils;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import com.isxcode.star.modules.workflow.repository.WorkflowInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;

@Service
@Slf4j
public class CurlExecutor extends WorkExecutor {

	private final IsxAppProperties isxAppProperties;

	public CurlExecutor(WorkInstanceRepository workInstanceRepository,
			WorkflowInstanceRepository workflowInstanceRepository, IsxAppProperties isxAppProperties) {

		super(workInstanceRepository, workflowInstanceRepository);
		this.isxAppProperties = isxAppProperties;
	}

	public void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance) {

		// 获取日志构造器
		StringBuilder logBuilder = workRunContext.getLogBuilder();

		// 判断执行脚本是否为空
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测脚本内容 \n");
		if (Strings.isEmpty(workRunContext.getScript())) {
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测脚本失败 : Curl内容为空不能执行  \n");
		}

		// 脚本检查通过
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行作业 \n");
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("执行作业内容: \n")
				.append(workRunContext.getScript()).append("\n");
		workInstance = updateInstance(workInstance, logBuilder);

		// 将脚本推送到本地
		String bashFile = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "work"
				+ File.separator + workRunContext.getTenantId() + File.separator + workInstance.getId() + ".sh";
		FileUtil.writeUtf8String(workRunContext.getScript() + " \\\n && echo 'zhiqingyun_success'", bashFile);

		// 执行命令
		String executeBashWorkCommand = "bash " + PathUtils.parseProjectPath(isxAppProperties.getResourcesPath())
				+ File.separator + "work" + File.separator + workRunContext.getTenantId() + File.separator
				+ workInstance.getId() + ".sh";
		String result = RuntimeUtil.execForStr(executeBashWorkCommand);

		// 保存运行日志
		workInstance.setYarnLog(result.replace("&& echo 'zhiqingyun_success'", "").replace("zhiqingyun_success", ""));
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("保存结果成功 \n");
		updateInstance(workInstance, logBuilder);

		// 删除脚本和日志
		try {
			String clearWorkRunFile = "rm -f " + PathUtils.parseProjectPath(isxAppProperties.getResourcesPath())
					+ File.separator + "work" + File.separator + workRunContext.getTenantId() + File.separator
					+ workInstance.getId() + ".sh";
			RuntimeUtil.execForStr(clearWorkRunFile);
		} catch (Exception e) {
			log.error("删除运行脚本失败");
		}

		// 判断脚本运行成功还是失败
		if (!result.contains("zhiqingyun_success") || result.contains("echo 'zhiqingyun_success'")) {
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "任务运行异常" + "\n");
		}
	}

	@Override
	protected void abort(WorkInstanceEntity workInstance) {

		Thread thread = WORK_THREAD.get(workInstance.getId());
		thread.interrupt();
	}
}
