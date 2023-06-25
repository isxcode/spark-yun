package com.isxcode.star.backend.module.work;

import com.isxcode.star.api.annotations.SuccessResponse;
import com.isxcode.star.api.constants.base.ModulePrefix;
import com.isxcode.star.api.constants.base.SecurityConstants;
import com.isxcode.star.api.pojos.work.req.WokAddWorkReq;
import com.isxcode.star.api.pojos.work.req.WokQueryWorkReq;
import com.isxcode.star.api.pojos.work.req.WokUpdateWorkReq;
import com.isxcode.star.api.pojos.work.res.WokGetDataRes;
import com.isxcode.star.api.pojos.work.res.WokGetStatusRes;
import com.isxcode.star.api.pojos.work.res.WokGetSubmitLogRes;
import com.isxcode.star.api.pojos.work.res.WokGetWorkLogRes;
import com.isxcode.star.api.pojos.work.res.WokGetWorkRes;
import com.isxcode.star.api.pojos.work.res.WokQueryWorkRes;
import com.isxcode.star.api.pojos.work.res.WokRunWorkRes;
import com.isxcode.star.backend.module.user.action.UserLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "作业模块")
@RestController
@RequestMapping(ModulePrefix.WORK)
@RequiredArgsConstructor
public class WorkController {

  private final WorkBizService workBizService;

  @UserLog
  @Operation(summary = "添加作业接口")
  @PostMapping("/addWork")
  @SuccessResponse("创建成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public void addWork(@Valid @RequestBody WokAddWorkReq addWorkReq) {

    workBizService.addWork(addWorkReq);
  }

  @UserLog
  @Operation(summary = "更新作业接口")
  @PostMapping("/updateWork")
  @SuccessResponse("更新成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public void updateWork(@Valid @RequestBody WokUpdateWorkReq wokUpdateWorkReq) {

    workBizService.updateWork(wokUpdateWorkReq);
  }

  @UserLog
  @Operation(summary = "运行作业接口")
  @GetMapping("/runWork")
  @SuccessResponse("提交成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public WokRunWorkRes runWork(
      @Schema(description = "作业唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4") @RequestParam
          String workId) {

    return workBizService.submitWork(workId);
  }

  @Operation(summary = "查询作业运行日志接口")
  @GetMapping("/getYarnLog")
  @SuccessResponse("查询成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public WokGetWorkLogRes getYarnLog(
      @Schema(description = "实例唯一id", example = "sy_12baf74d710c43a78858e547bf41a586") @RequestParam
          String instanceId) {

    return workBizService.getWorkLog(instanceId);
  }

  @UserLog
  @Operation(summary = "查询作业返回数据接口")
  @GetMapping("/getData")
  @SuccessResponse("查询成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public WokGetDataRes getData(
      @Schema(description = "实例唯一id", example = "sy_12baf74d710c43a78858e547bf41a586") @RequestParam
          String instanceId) {

    return workBizService.getData(instanceId);
  }

  @UserLog
  @Operation(summary = "中止作业接口")
  @GetMapping("/stopJob")
  @SuccessResponse("中止成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public void stopJob(
      @Schema(description = "实例唯一id", example = "sy_12baf74d710c43a78858e547bf41a586") @RequestParam
          String instanceId) {

    workBizService.stopJob(instanceId);
  }

  @UserLog
  @Operation(summary = "获取作业当前运行状态接口")
  @GetMapping("/getStatus")
  @SuccessResponse("获取成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public WokGetStatusRes getStatus(
      @Schema(description = "实例唯一id", example = "sy_12baf74d710c43a78858e547bf41a586") @RequestParam
          String instanceId) {

    return workBizService.getStatus(instanceId);
  }

  @UserLog
  @Operation(summary = "删除作业接口")
  @GetMapping("/delWork")
  @SuccessResponse("删除成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public void delWork(
      @Schema(description = "作业唯一id", example = "sy_12baf74d710c43a78858e547bf41a586") @RequestParam
          String workId) {

    workBizService.delWork(workId);
  }

  @UserLog
  @Operation(summary = "查询作业列表接口")
  @PostMapping("/queryWork")
  @SuccessResponse("查询成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public Page<WokQueryWorkRes> queryWork(@Valid @RequestBody WokQueryWorkReq wocQueryWorkReq) {

    return workBizService.queryWork(wocQueryWorkReq);
  }

  @UserLog
  @Operation(summary = "获取作业信息接口")
  @GetMapping("/getWork")
  @SuccessResponse("获取成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public WokGetWorkRes getWork(
      @Schema(description = "作业唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4") @RequestParam
          String workId) {

    return workBizService.getWork(workId);
  }

  @Operation(summary = "查询作业提交日志接口")
  @GetMapping("/getSubmitLog")
  @SuccessResponse("查询成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public WokGetSubmitLogRes getSubmitLog(
      @Schema(description = "实例唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4") @RequestParam
          String instanceId) {

    return workBizService.getSubmitLog(instanceId);
  }
}
