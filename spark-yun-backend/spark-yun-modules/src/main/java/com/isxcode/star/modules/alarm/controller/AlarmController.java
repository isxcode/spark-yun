package com.isxcode.star.modules.alarm.controller;

import com.isxcode.star.api.alarm.req.*;
import com.isxcode.star.api.alarm.res.CheckMessageRes;
import com.isxcode.star.api.alarm.res.PageAlarmInstanceRes;
import com.isxcode.star.api.alarm.res.PageAlarmRes;
import com.isxcode.star.api.alarm.res.PageMessageRes;
import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.api.user.constants.RoleType;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.alarm.service.AlarmBizService;
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

@Tag(name = "基线告警模块")
@RequestMapping(ModuleCode.ALARM)
@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmBizService alarmBizService;

    @Operation(summary = "添加消息体接口")
    @PostMapping("/addMessage")
    @SuccessResponse("添加成功")
    public void addMessage(@Valid @RequestBody AddMessageReq addMessageReq) {

        alarmBizService.addMessage(addMessageReq);
    }

    @Operation(summary = "更新消息体接口")
    @PostMapping("/updateMessage")
    @SuccessResponse("更新成功")
    public void updateMessage(@Valid @RequestBody UpdateMessageReq updateMessageReq) {

        alarmBizService.updateMessage(updateMessageReq);
    }

    @Operation(summary = "分页查询消息体接口")
    @PostMapping("/pageMessage")
    @SuccessResponse("查询成功")
    public Page<PageMessageRes> pageMessage(@Valid @RequestBody PageMessageReq pageMessageReq) {

        return alarmBizService.pageMessage(pageMessageReq);
    }

    @Secured({RoleType.TENANT_ADMIN})
    @Operation(summary = "删除消息体接口")
    @PostMapping("/deleteMessage")
    @SuccessResponse("删除成功")
    public void deleteMessage(@Valid @RequestBody DeleteMessageReq deleteMessageReq) {

        alarmBizService.deleteMessage(deleteMessageReq);
    }

    @Operation(summary = "启动消息体接口")
    @PostMapping("/enableMessage")
    @SuccessResponse("启用成功")
    public void enableMessage(@Valid @RequestBody EnableMessageReq enableMessageReq) {

        alarmBizService.enableMessage(enableMessageReq);
    }

    @Operation(summary = "禁用消息体接口")
    @PostMapping("/disableMessage")
    @SuccessResponse("禁用成功")
    public void disableMessage(@Valid @RequestBody DisableMessageReq disableMessageReq) {

        alarmBizService.disableMessage(disableMessageReq);
    }

    @Operation(summary = "检测消息体接口")
    @PostMapping("/checkMessage")
    @SuccessResponse("检测完成")
    public CheckMessageRes checkMessage(@Valid @RequestBody CheckMessageReq checkMessageReq) {

        return alarmBizService.checkMessage(checkMessageReq);
    }

    @Operation(summary = "添加告警接口")
    @PostMapping("/addAlarm")
    @SuccessResponse("添加成功")
    public void addAlarm(@Valid @RequestBody AddAlarmReq addAlarmReq) {

        alarmBizService.addAlarm(addAlarmReq);
    }

    @Operation(summary = "更新告警接口")
    @PostMapping("/updateAlarm")
    @SuccessResponse("更新成功")
    public void updateAlarm(@Valid @RequestBody UpdateAlarmReq updateAlarmReq) {

        alarmBizService.updateAlarm(updateAlarmReq);
    }

    @Operation(summary = "分页查询告警接口")
    @PostMapping("/pageAlarm")
    @SuccessResponse("查询成功")
    public Page<PageAlarmRes> pageAlarm(@Valid @RequestBody PageAlarmReq pageAlarmReq) {

        return alarmBizService.pageAlarm(pageAlarmReq);
    }

    @Secured({RoleType.TENANT_ADMIN})
    @Operation(summary = "删除告警接口")
    @PostMapping("/deleteAlarm")
    @SuccessResponse("删除成功")
    public void deleteAlarm(@Valid @RequestBody DeleteAlarmReq deleteAlarmReq) {

        alarmBizService.deleteAlarm(deleteAlarmReq);
    }

    @Operation(summary = "启动告警接口")
    @PostMapping("/enableAlarm")
    @SuccessResponse("启用成功")
    public void enableAlarm(@Valid @RequestBody EnableAlarmReq enableAlarmReq) {

        alarmBizService.enableAlarm(enableAlarmReq);
    }

    @Operation(summary = "禁用告警接口")
    @PostMapping("/disableAlarm")
    @SuccessResponse("禁用成功")
    public void disableAlarm(@Valid @RequestBody DisableAlarmReq disableAlarmReq) {

        alarmBizService.disableAlarm(disableAlarmReq);
    }

    @Operation(summary = "分页查询告警实例接口")
    @PostMapping("/pageAlarmInstance")
    @SuccessResponse("查询成功")
    public Page<PageAlarmInstanceRes> pageAlarmInstance(@Valid @RequestBody PageAlarmInstanceReq pageAlarmInstanceReq) {

        return alarmBizService.pageAlarmInstance(pageAlarmInstanceReq);
    }

    @Operation(summary = "删除告警实例接口")
    @PostMapping("/deleteAlarmInstance")
    @SuccessResponse("删除成功")
    public void deleteAlarmInstance(@Valid @RequestBody DeleteAlarmInstanceReq deleteAlarmInstanceReq) {

        alarmBizService.deleteAlarmInstance(deleteAlarmInstanceReq);
    }
}
