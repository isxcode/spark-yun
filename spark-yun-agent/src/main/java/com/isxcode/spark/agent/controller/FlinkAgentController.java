package com.isxcode.spark.agent.controller;

import com.isxcode.spark.agent.service.FlinkAgentBizService;
import com.isxcode.spark.api.agent.constants.FlinkAgentUrl;
import com.isxcode.spark.api.agent.req.flink.GetWorkInfoReq;
import com.isxcode.spark.api.agent.req.flink.GetWorkLogReq;
import com.isxcode.spark.api.agent.req.flink.StopWorkReq;
import com.isxcode.spark.api.agent.req.flink.SubmitWorkReq;
import com.isxcode.spark.api.agent.res.flink.GetWorkInfoRes;
import com.isxcode.spark.api.agent.res.flink.GetWorkLogRes;
import com.isxcode.spark.api.agent.res.flink.StopWorkRes;
import com.isxcode.spark.api.agent.res.flink.SubmitWorkRes;
import com.isxcode.spark.common.annotations.successResponse.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.immutables.value.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "至轻云代理模块")
@RestController
@RequiredArgsConstructor
public class FlinkAgentController {

    private final FlinkAgentBizService flinkAgentBizService;

    @Operation(summary = "提交作业接口")
    @PostMapping(FlinkAgentUrl.SUBMIT_WORK_URL)
    @SuccessResponse("提交成功")
    public SubmitWorkRes submitWork(@Value @RequestBody SubmitWorkReq submitWorkReq) {

        return flinkAgentBizService.submitWork(submitWorkReq);
    }

    @Operation(summary = "获取作业信息接口")
    @PostMapping(FlinkAgentUrl.GET_WORK_INFO_URL)
    @SuccessResponse("获取成功")
    public GetWorkInfoRes getWorkInfo(@Value @RequestBody GetWorkInfoReq getWorkInfoReq) {

        return flinkAgentBizService.getWorkInfo(getWorkInfoReq);
    }

    @Operation(summary = "获取日志接口")
    @PostMapping(FlinkAgentUrl.GET_WORK_LOG_URL)
    @SuccessResponse("获取成功")
    public GetWorkLogRes getWorkLog(@Value @RequestBody GetWorkLogReq getWorkLogReq) {

        return flinkAgentBizService.getWorkLog(getWorkLogReq);
    }

    @Operation(summary = "中止作业接口")
    @PostMapping(FlinkAgentUrl.STOP_WORK_URL)
    @SuccessResponse("中止成功")
    public StopWorkRes stopWork(@Value @RequestBody StopWorkReq stopWorkReq) {

        return flinkAgentBizService.stopWork(stopWorkReq);
    }
}
