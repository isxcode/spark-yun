package com.isxcode.star.modules.alarm.controller;

import com.isxcode.star.api.alarm.req.*;
import com.isxcode.star.api.alarm.res.PageMessageRes;
import com.isxcode.star.api.cluster.pojos.req.*;
import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.alarm.service.AlarmBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "基线告警模块")
@RestController
@RequestMapping(ModuleCode.ALARM)
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
	@SuccessResponse("检测成功")
	public void checkMessage(@Valid @RequestBody CheckMessageReq checkMessageReq) {

		alarmBizService.checkMessage(checkMessageReq);
	}
	//
	//
	// @Operation(summary = "添加告警接口")
	// @PostMapping("/addAlarm")
	// @SuccessResponse("添加成功")
	// public void addAlarm(@Valid @RequestBody AddClusterReq addClusterReq) {
	//
	// alarmBizService.addAlarm(addClusterReq);
	// }
	//
	// @Operation(summary = "更新告警接口")
	// @PostMapping("/updateAlarm")
	// @SuccessResponse("更新成功")
	// public void updateAlarm(@Valid @RequestBody UpdateClusterReq
	// updateClusterReq) {
	//
	// alarmBizService.updateAlarm(updateClusterReq);
	// }
	//
	// @Operation(summary = "分页查询告警接口")
	// @PostMapping("/pageAlarm")
	// @SuccessResponse("查询成功")
	// public Page<PageClusterRes> pageAlarm(@Valid @RequestBody PageClusterReq
	// pageClusterReq) {
	//
	// return alarmBizService.pageAlarm(pageClusterReq);
	// }
	//
	// @Operation(summary = "删除告警接口")
	// @PostMapping("/deleteAlarm")
	// @SuccessResponse("删除成功")
	// public void deleteAlarm(@Valid @RequestBody DeleteClusterReq
	// deleteClusterReq) {
	//
	// alarmBizService.deleteAlarm(deleteClusterReq);
	// }
	//
	// @Operation(summary = "启动告警接口")
	// @PostMapping("/enableAlarm")
	// @SuccessResponse("启用成功")
	// public void enableAlarm(@Valid @RequestBody CheckClusterReq checkClusterReq)
	// {
	//
	// alarmBizService.enableAlarm(checkClusterReq);
	// }
	//
	// @Operation(summary = "禁用告警接口")
	// @PostMapping("/disableAlarm")
	// @SuccessResponse("禁用成功")
	// public void disableAlarm(@Valid @RequestBody CheckClusterReq checkClusterReq)
	// {
	//
	// alarmBizService.disableAlarm(checkClusterReq);
	// }
}
