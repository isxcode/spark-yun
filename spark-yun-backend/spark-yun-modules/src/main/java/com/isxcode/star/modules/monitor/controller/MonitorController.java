package com.isxcode.star.modules.monitor.controller;

import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.api.monitor.req.GetClusterMonitorReq;
import com.isxcode.star.api.monitor.req.GetInstanceMonitorReq;
import com.isxcode.star.api.monitor.req.PageInstancesReq;
import com.isxcode.star.api.monitor.res.GetClusterMonitorRes;
import com.isxcode.star.api.monitor.res.GetInstanceMonitorRes;
import com.isxcode.star.api.monitor.res.GetSystemMonitorRes;
import com.isxcode.star.api.monitor.res.PageInstancesRes;
import com.isxcode.star.api.user.constants.RoleType;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.monitor.service.MonitorBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "监控模块")
@RequestMapping(ModuleCode.MONITOR)
@RestController
@RequiredArgsConstructor
public class MonitorController {

    private final MonitorBizService monitorBizService;

    @Secured({RoleType.NORMAL_MEMBER})
    @Operation(summary = "获取系统基础信息接口")
    @PostMapping("/getSystemBaseInfo")
    @SuccessResponse("查询成功")
    public GetSystemMonitorRes getSystemMonitor() {

        return monitorBizService.getSystemMonitor();
    }

    @Secured({RoleType.NORMAL_MEMBER})
    @Operation(summary = "获取集群监控数据接口")
    @PostMapping("/getClusterMonitor")
    @SuccessResponse("查询成功")
    public GetClusterMonitorRes getClusterMonitor(@RequestBody GetClusterMonitorReq getClusterMonitorReq) {

        return monitorBizService.getClusterMonitor(getClusterMonitorReq);
    }

    @Secured({RoleType.NORMAL_MEMBER})
    @Operation(summary = "添加实例图接口")
    @PostMapping("/getInstanceMonitor")
    @SuccessResponse("查询成功")
    public GetInstanceMonitorRes getInstanceMonitor(@Valid @RequestBody GetInstanceMonitorReq getInstanceMonitorReq) {

        return monitorBizService.getInstanceMonitor(getInstanceMonitorReq);
    }

    @Secured({RoleType.NORMAL_MEMBER})
    @Operation(summary = "查询实例列表接口")
    @PostMapping("/pageInstances")
    @SuccessResponse("查询成功")
    public Page<PageInstancesRes> pageInstances(@Valid @RequestBody PageInstancesReq pageInstancesReq) {

        return monitorBizService.pageInstances(pageInstancesReq);
    }
}
