package com.isxcode.star.modules.work.controller;

import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.api.work.pojos.req.*;
import com.isxcode.star.api.work.pojos.res.*;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.work.service.biz.SyncWorkConfigBizService;
import com.isxcode.star.modules.work.service.biz.WorkBizService;
import com.isxcode.star.modules.work.service.biz.WorkConfigBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "作业模块")
@RestController
@RequestMapping(ModuleCode.WORK)
@RequiredArgsConstructor
public class WorkController {

	private final WorkBizService workBizService;

	private final WorkConfigBizService workConfigBizService;

  private final SyncWorkConfigBizService syncWorkConfigBizService;

	@Operation(summary = "添加作业接口")
	@PostMapping("/addWork")
	@SuccessResponse("创建成功")
	public void addWork(@Valid @RequestBody AddWorkReq addWorkReq) {

		workBizService.addWork(addWorkReq);
	}

	@Operation(summary = "更新作业接口")
	@PostMapping("/updateWork")
	@SuccessResponse("更新成功")
	public void updateWork(@Valid @RequestBody UpdateWorkReq updateWorkReq) {

		workBizService.updateWork(updateWorkReq);
	}

	@Operation(summary = "运行作业接口")
	@PostMapping("/runWork")
	@SuccessResponse("提交成功")
	public RunWorkRes runWork(@Valid @RequestBody RunWorkReq runWorkReq) {

		return workBizService.runWork(runWorkReq);
	}

	@Operation(summary = "查询作业运行日志接口")
	@PostMapping("/getYarnLog")
	@SuccessResponse("查询成功")
	public GetWorkLogRes getYarnLog(@Valid @RequestBody GetYarnLogReq getYarnLogReq) {

		return workBizService.getWorkLog(getYarnLogReq);
	}

	@Operation(summary = "查询作业返回数据接口")
	@PostMapping("/getData")
	@SuccessResponse("查询成功")
	public GetDataRes getData(@Valid @RequestBody GetDataReq getDataReq) {

		return workBizService.getData(getDataReq);
	}

	@Operation(summary = "中止作业接口")
	@PostMapping("/stopJob")
	@SuccessResponse("中止成功")
	public void stopJob(@Valid @RequestBody StopJobReq stopJobReq) {

		workBizService.stopJob(stopJobReq);
	}

	@Operation(summary = "获取作业当前运行状态接口")
	@PostMapping("/getStatus")
	@SuccessResponse("获取成功")
	public GetStatusRes getStatus(@Valid @RequestBody GetStatusReq getStatusReq) {

		return workBizService.getStatus(getStatusReq);
	}

	@Operation(summary = "删除作业接口")
	@PostMapping("/deleteWork")
	@SuccessResponse("删除成功")
	public void deleteWork(@Valid @RequestBody DeleteWorkReq deleteWorkReq) {

		workBizService.deleteWork(deleteWorkReq);
	}

	@Operation(summary = "查询作业列表接口")
	@PostMapping("/pageWork")
	@SuccessResponse("查询成功")
	public Page<PageWorkRes> pageWork(@Valid @RequestBody PageWorkReq pageWorkReq) {

		return workBizService.pageWork(pageWorkReq);
	}

	@Operation(summary = "获取作业信息接口")
	@PostMapping("/getWork")
	@SuccessResponse("获取成功")
	public GetWorkRes getWork(@Valid @RequestBody GetWorkReq getWorkReq) {

		return workBizService.getWork(getWorkReq);
	}

	@Operation(summary = "查询作业提交日志接口")
	@PostMapping("/getSubmitLog")
	@SuccessResponse("查询成功")
	public GetSubmitLogRes getSubmitLog(@Valid @RequestBody GetSubmitLogReq getSubmitLogReq) {

		return workBizService.getSubmitLog(getSubmitLogReq);
	}

	@Operation(summary = "作业重命名接口")
	@PostMapping("/renameWork")
	@SuccessResponse("修改成功")
	public void renameWork(@Valid @RequestBody RenameWorkReq renameWorkReq) {

		workBizService.renameWork(renameWorkReq);
	}

	@Operation(summary = "作业复制接口")
	@PostMapping("/copyWork")
	@SuccessResponse("复制成功")
	public void copyWork(@Valid @RequestBody CopyWorkReq copyWorkReq) {

		workBizService.copyWork(copyWorkReq);
	}

	@Operation(summary = "作业置顶接口")
	@PostMapping("/topWork")
	@SuccessResponse("置顶成功")
	public void topWork(@Valid @RequestBody TopWorkReq topWorkReq) {

		workBizService.topWork(topWorkReq);
	}

	@Operation(summary = "配置作业接口")
	@PostMapping("/configWork")
	@SuccessResponse("保存成功")
	public void configWork(@Valid @RequestBody ConfigWorkReq configWorkReq) {

		workConfigBizService.configWork(configWorkReq);
	}

  @Operation(summary = "获取同步作业配置接口")
  @PostMapping("/getSyncWorkConfig")
  @SuccessResponse("查询成功")
  public GetSyncWorkConfigRes getSyncWorkConfig(@Valid @RequestBody GetSyncWorkConfigReq getSyncWorkConfigReq) {

    return syncWorkConfigBizService.getSyncWorkConfig(getSyncWorkConfigReq);
  }

  @Operation(summary = "配置作业接口")
  @PostMapping("/saveSyncWorkConfig")
  @SuccessResponse("保存成功")
  public void saveSyncWorkConfig(@Valid @RequestBody SaveSyncWorkConfigReq saveSyncWorkConfigReq) {

    syncWorkConfigBizService.saveSyncWorkConfig(saveSyncWorkConfigReq);
  }

}
