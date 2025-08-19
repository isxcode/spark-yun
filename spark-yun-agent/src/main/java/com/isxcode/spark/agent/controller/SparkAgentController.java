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

@Tag(name = "至轻云代理模块")
@RestController
@RequiredArgsConstructor
public class SparkAgentController {

    private final SparkAgentBizService sparkYunAgentBizService;

    @Operation(summary = "提交作业接口")
    @PostMapping(SparkAgentUrl.SUBMIT_WORK_URL)
    @SuccessResponse("提交成功")
    public SubmitWorkRes submitWork(@Valid @RequestBody SubmitWorkReq submitWorkReq) {

        return sparkYunAgentBizService.submitWork(submitWorkReq);
    }

    @Operation(summary = "获取作业状态接口")
    @PostMapping(SparkAgentUrl.GET_WORK_STATUS_URL)
    @SuccessResponse("获取成功")
    public GetWorkStatusRes getWorkStatus(@Valid @RequestBody GetWorkStatusReq getWorkStatusReq) {

        return sparkYunAgentBizService.getWorkStatus(getWorkStatusReq);
    }

    @Operation(summary = "获取返回数据接口")
    @PostMapping(SparkAgentUrl.GET_WORK_DATA_URL)
    @SuccessResponse("获取成功")
    public GetWorkDataRes getWorkData(@Valid @RequestBody GetWorkDataReq getWorkDataReq) {

        return sparkYunAgentBizService.getWorkData(getWorkDataReq);
    }

    @Operation(summary = "获取Stdout全部日志接口")
    @PostMapping(SparkAgentUrl.GET_ALL_WORK_STDOUT_LOG_URL)
    @SuccessResponse("获取成功")
    public GetWorkStdoutLogRes getAllWorkStdoutLog(@Valid @RequestBody GetWorkStdoutLogReq getWorkStdoutLogReq) {

        return sparkYunAgentBizService.getAllWorkStdoutLog(getWorkStdoutLogReq);
    }

    @Operation(summary = "获取Stdout日志接口")
    @PostMapping(SparkAgentUrl.GET_CUSTOM_WORK_STDOUT_LOG_URL)
    @SuccessResponse("获取成功")
    public GetWorkStdoutLogRes getCustomWorkStdoutLog(@Valid @RequestBody GetWorkStdoutLogReq getWorkStdoutLogReq) {

        return sparkYunAgentBizService.getCustomWorkStdoutLog(getWorkStdoutLogReq);
    }

    @Operation(summary = "获取Stdout日志接口")
    @PostMapping(SparkAgentUrl.GET_WORK_STDOUT_LOG_URL)
    @SuccessResponse("获取成功")
    public GetWorkStdoutLogRes getWorkStdoutLog(@Valid @RequestBody GetWorkStdoutLogReq getWorkStdoutLogReq) {

        return sparkYunAgentBizService.getWorkStdoutLog(getWorkStdoutLogReq);
    }

    @Operation(summary = "获取Stderr日志接口")
    @PostMapping(SparkAgentUrl.GET_WORK_STDERR_LOG_URL)
    @SuccessResponse("获取成功")
    public GetWorkStderrLogRes getWorkStderrLog(@Valid @RequestBody GetWorkStderrLogReq getWorkStderrLogReq) {

        return sparkYunAgentBizService.getWorkStderrLog(getWorkStderrLogReq);
    }

    @Operation(summary = "中止作业接口")
    @PostMapping(SparkAgentUrl.STOP_WORK_URL)
    @SuccessResponse("中止成功")
    public void stopWork(@Valid @RequestBody StopWorkReq stopWorkReq) {

        sparkYunAgentBizService.stopWork(stopWorkReq);
    }

    @Operation(summary = "心跳检测接口")
    @PostMapping(SparkAgentUrl.HEART_CHECK_URL)
    @SuccessResponse("正常心跳")
    public void heartCheck() {}

}
