package com.isxcode.star.agent.controller;

import com.isxcode.star.agent.service.SparkYunAgentBizService;
import com.isxcode.star.api.agent.constants.AgentUrl;
import com.isxcode.star.api.agent.req.*;
import com.isxcode.star.api.agent.res.*;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "至轻云代理模块")
@RestController
@RequiredArgsConstructor
public class SparkYunAgentController {

    private final SparkYunAgentBizService sparkYunAgentBizService;

    @Operation(summary = "提交作业接口")
    @PostMapping(AgentUrl.SUBMIT_WORK_URL)
    @SuccessResponse("提交成功")
    public SubmitWorkRes submitWork(@Valid @RequestBody SubmitWorkReq submitWorkReq) {

        return sparkYunAgentBizService.submitWork(submitWorkReq);
    }

    @Operation(summary = "获取作业状态接口")
    @PostMapping(AgentUrl.GET_WORK_STATUS_URL)
    @SuccessResponse("获取成功")
    public GetWorkStatusRes getWorkStatus(@Valid @RequestBody GetWorkStatusReq getWorkStatusReq) {

        return sparkYunAgentBizService.getWorkStatus(getWorkStatusReq);
    }

    @Operation(summary = "获取返回数据接口")
    @PostMapping(AgentUrl.GET_WORK_DATA_URL)
    @SuccessResponse("获取成功")
    public GetWorkDataRes getWorkData(@Valid @RequestBody GetWorkDataReq getWorkDataReq) {

        return sparkYunAgentBizService.getWorkData(getWorkDataReq);
    }

    @Operation(summary = "获取Stdout全部日志接口")
    @PostMapping(AgentUrl.GET_ALL_WORK_STDOUT_LOG_URL)
    @SuccessResponse("获取成功")
    public GetWorkStdoutLogRes getAllWorkStdoutLog(@Valid @RequestBody GetWorkStdoutLogReq getWorkStdoutLogReq) {

        return sparkYunAgentBizService.getAllWorkStdoutLog(getWorkStdoutLogReq);
    }

    @Operation(summary = "获取Stdout日志接口")
    @PostMapping(AgentUrl.GET_CUSTOM_WORK_STDOUT_LOG_URL)
    @SuccessResponse("获取成功")
    public GetWorkStdoutLogRes getCustomWorkStdoutLog(@Valid @RequestBody GetWorkStdoutLogReq getWorkStdoutLogReq) {

        return sparkYunAgentBizService.getCustomWorkStdoutLog(getWorkStdoutLogReq);
    }

    @Operation(summary = "获取Stdout日志接口")
    @PostMapping(AgentUrl.GET_WORK_STDOUT_LOG_URL)
    @SuccessResponse("获取成功")
    public GetWorkStdoutLogRes getWorkStdoutLog(@Valid @RequestBody GetWorkStdoutLogReq getWorkStdoutLogReq) {

        return sparkYunAgentBizService.getWorkStdoutLog(getWorkStdoutLogReq);
    }

    @Operation(summary = "获取Stderr日志接口")
    @PostMapping(AgentUrl.GET_WORK_STDERR_LOG_URL)
    @SuccessResponse("获取成功")
    public GetWorkStderrLogRes getWorkStderrLog(@Valid @RequestBody GetWorkStderrLogReq getWorkStderrLogReq) {

        return sparkYunAgentBizService.getWorkStderrLog(getWorkStderrLogReq);
    }

    @Operation(summary = "中止作业接口")
    @PostMapping(AgentUrl.STOP_WORK_URL)
    @SuccessResponse("中止成功")
    public void stopWork(@Valid @RequestBody StopWorkReq stopWorkReq) {

        sparkYunAgentBizService.stopWork(stopWorkReq);
    }

    @Operation(summary = "心跳检测接口")
    @PostMapping(AgentUrl.HEART_CHECK_URL)
    @SuccessResponse("正常心跳")
    public void heartCheck() {}

    @Operation(summary = "计算容器心跳检测接口")
    @PostMapping(AgentUrl.CONTAINER_CHECK_URL)
    public ContainerCheckRes containerCheck(@RequestBody ContainerCheckReq containerCheckReq) {

        return sparkYunAgentBizService.containerCheck(containerCheckReq);
    }

    @Operation(summary = "计算容器执行sql接口")
    @PostMapping(AgentUrl.EXECUTE_CONTAINER_SQL_URL)
    public ExecuteContainerSqlRes executeContainerSql(@RequestBody ExecuteContainerSqlReq executeContainerSqlReq) {

        return sparkYunAgentBizService.executeContainerSql(executeContainerSqlReq);
    }

    @Operation(summary = "提交计算容器接口")
    @PostMapping(AgentUrl.DEPLOY_CONTAINER_URL)
    @SuccessResponse("提交成功")
    public DeployContainerRes deployContainer(@Valid @RequestBody SubmitWorkReq submitWorkReq) {

        return sparkYunAgentBizService.deployContainer(submitWorkReq);
    }
}
