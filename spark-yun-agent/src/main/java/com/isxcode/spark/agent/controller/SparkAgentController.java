package com.isxcode.spark.agent.controller;

import com.isxcode.spark.agent.service.SparkAgentBizService;
import com.isxcode.spark.api.agent.constants.SparkAgentUrl;
import com.isxcode.spark.api.agent.req.spark.*;
import com.isxcode.spark.api.agent.res.spark.*;
import com.isxcode.spark.common.annotations.successResponse.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "Spark相关代理模块")
@RestController
@RequiredArgsConstructor
public class SparkAgentController {

    private final SparkAgentBizService sparkYunAgentBizService;

    @Operation(summary = "提交作业")
    @PostMapping(SparkAgentUrl.SUBMIT_WORK_URL)
    @SuccessResponse("提交成功")
    public SubmitWorkRes submitWork(@Valid @RequestBody SubmitWorkReq submitWorkReq) {

        return sparkYunAgentBizService.submitWork(submitWorkReq);
    }

    @Operation(summary = "获取作业状态")
    @PostMapping(SparkAgentUrl.GET_WORK_STATUS_URL)
    @SuccessResponse("获取成功")
    public GetWorkInfoRes getWorkStatus(@Valid @RequestBody GetWorkStatusReq getWorkStatusReq) {

        return sparkYunAgentBizService.getWorkInfo(getWorkStatusReq);
    }

    @Operation(summary = "获取返回数据")
    @PostMapping(SparkAgentUrl.GET_WORK_DATA_URL)
    @SuccessResponse("获取成功")
    public GetWorkDataRes getWorkData(@Valid @RequestBody GetWorkDataReq getWorkDataReq) {

        return sparkYunAgentBizService.getWorkData(getWorkDataReq);
    }

    @Operation(summary = "获取自定义Jar作业Stdout日志")
    @PostMapping(SparkAgentUrl.GET_CUSTOM_JAR_WORK_STDOUT_LOG_URL)
    @SuccessResponse("获取成功")
    public GetWorkStdoutLogRes getCustomJarWorkStdoutLog(@Valid @RequestBody GetWorkStdoutLogReq getWorkStdoutLogReq) {

        return sparkYunAgentBizService.getCustomJarWorkStdoutLog(getWorkStdoutLogReq);
    }

    @Operation(summary = "获取Stdout日志")
    @PostMapping(SparkAgentUrl.GET_WORK_STDOUT_LOG_URL)
    @SuccessResponse("获取成功")
    public GetWorkStdoutLogRes getWorkStdoutLog(@Valid @RequestBody GetWorkStdoutLogReq getWorkStdoutLogReq) {

        return sparkYunAgentBizService.getWorkStdoutLog(getWorkStdoutLogReq);
    }

    @Operation(summary = "获取Stderr日志")
    @PostMapping(SparkAgentUrl.GET_WORK_STDERR_LOG_URL)
    @SuccessResponse("获取成功")
    public GetWorkStderrLogRes getWorkStderrLog(@Valid @RequestBody GetWorkStderrLogReq getWorkStderrLogReq) {

        return sparkYunAgentBizService.getWorkStderrLog(getWorkStderrLogReq);
    }

    @Operation(summary = "获取最后一行Stdout日志")
    @PostMapping(SparkAgentUrl.GET_LAST_LINE_WORK_STDOUT_LOG_URL)
    @SuccessResponse("获取成功")
    public GetWorkStdoutLogRes getLastLineWorkStdoutLog(@Valid @RequestBody GetWorkStdoutLogReq getWorkStdoutLogReq) {

        return sparkYunAgentBizService.getLastLineWorkStdoutLog(getWorkStdoutLogReq);
    }

    @Operation(summary = "中止作业")
    @PostMapping(SparkAgentUrl.STOP_WORK_URL)
    @SuccessResponse("中止成功")
    public void stopWork(@Valid @RequestBody StopWorkReq stopWorkReq) {

        sparkYunAgentBizService.stopWork(stopWorkReq);
    }

    @Operation(summary = "代理服务心跳检测")
    @PostMapping(SparkAgentUrl.HEART_CHECK_URL)
    @SuccessResponse("正常心跳")
    public void heartCheck() {}

    @Operation(summary = "计算容器心跳检测")
    @PostMapping(SparkAgentUrl.CONTAINER_CHECK_URL)
    public ContainerCheckRes containerCheck(@RequestBody ContainerCheckReq containerCheckReq) {

        return sparkYunAgentBizService.containerCheck(containerCheckReq);
    }

    @Operation(summary = "执行计算容器SQL")
    @PostMapping(SparkAgentUrl.EXECUTE_CONTAINER_SQL_URL)
    public ExecuteContainerSqlRes executeContainerSql(@RequestBody ExecuteContainerSqlReq executeContainerSqlReq) {

        return sparkYunAgentBizService.executeContainerSql(executeContainerSqlReq);
    }

    @Operation(summary = "部署计算容器")
    @PostMapping(SparkAgentUrl.DEPLOY_CONTAINER_URL)
    @SuccessResponse("提交成功")
    public DeployContainerRes deployContainer(@Valid @RequestBody SubmitWorkReq submitWorkReq) {

        return sparkYunAgentBizService.deployContainer(submitWorkReq);
    }
}
