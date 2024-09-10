package com.isxcode.star.agent.controller;

import com.isxcode.star.agent.service.SparkYunAgentBizService;
import com.isxcode.star.api.agent.pojos.req.*;
import com.isxcode.star.api.agent.pojos.res.*;
import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "至轻云代理模块")
@RequestMapping(ModuleCode.SPARK_YUN_AGENT)
@RestController
@RequiredArgsConstructor
public class SparkYunAgentController {

    private final SparkYunAgentBizService sparkYunAgentBizService;

    @Operation(summary = "提交作业接口")
    @PostMapping("/submitWork")
    @SuccessResponse("提交成功")
    public SubmitWorkRes submitWork(@Valid @RequestBody SubmitWorkReq submitWorkReq) {

        return sparkYunAgentBizService.submitWork(submitWorkReq);
    }

    @Operation(summary = "获取作业状态接口")
    @GetMapping("/getWorkStatus")
    @SuccessResponse("获取成功")
    public GetWorkStatusRes getWorkStatus(@Valid @RequestBody GetWorkStatusReq getWorkStatusReq) {

        return sparkYunAgentBizService.getWorkStatus(getWorkStatusReq);
    }

    @Operation(summary = "获取返回数据接口")
    @GetMapping("/getWorkData")
    @SuccessResponse("获取成功")
    public GetWorkDataRes getWorkData(@Valid @RequestBody GetWorkDataReq getWorkDataReq) {

        return sparkYunAgentBizService.getWorkData(getWorkDataReq);
    }

    @Operation(summary = "获取Stdout日志接口")
    @GetMapping("/getWorkStdoutLog")
    @SuccessResponse("获取成功")
    public GetWorkStdoutLogRes getWorkStdoutLog(@Valid @RequestBody GetWorkStdoutLogReq getWorkStdoutLogReq) {

        return sparkYunAgentBizService.getWorkStdoutLog(getWorkStdoutLogReq);
    }

    @Operation(summary = "获取Stderr日志接口")
    @GetMapping("/getWorkStderrLog")
    @SuccessResponse("获取成功")
    public GetWorkStderrLogRes getWorkStderrLog(@Valid @RequestBody GetWorkStderrLogReq getWorkStderrLogReq) {

        return sparkYunAgentBizService.getWorkStderrLog(getWorkStderrLogReq);
    }

    @Operation(summary = "中止作业接口")
    @GetMapping("/stopWork")
    @SuccessResponse("中止成功")
    public void stopWork(@Valid @RequestBody StopWorkReq stopWorkReq) {

        sparkYunAgentBizService.stopWork(stopWorkReq);
    }

    @Operation(summary = "心跳检测接口")
    @GetMapping("/heartCheck")
    @SuccessResponse("正常心跳")
    public void heartCheck() {}

    @Operation(summary = "计算容器心跳检测接口")
    @PostMapping("/containerCheck")
    public ContainerCheckRes containerCheck(@RequestBody ContainerCheckReq containerCheckReq) {

        return sparkYunAgentBizService.containerCheck(containerCheckReq);
    }

    @Operation(summary = "计算容器执行sql接口")
    @PostMapping("/executeContainerSql")
    public ExecuteContainerSqlRes executeContainerSql(@RequestBody ExecuteContainerSqlReq executeContainerSqlReq) {

        return sparkYunAgentBizService.executeContainerSql(executeContainerSqlReq);
    }
}
