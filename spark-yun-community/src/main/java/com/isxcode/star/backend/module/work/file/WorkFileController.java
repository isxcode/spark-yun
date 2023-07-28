package com.isxcode.star.backend.module.work.file;

import com.isxcode.star.api.annotations.SuccessResponse;
import com.isxcode.star.api.constants.base.ModulePrefix;
import com.isxcode.star.api.constants.base.SecurityConstants;
import com.isxcode.star.api.pojos.work.file.req.WofFileListReq;
import com.isxcode.star.api.pojos.work.file.res.WofAddFileRes;
import com.isxcode.star.api.pojos.work.file.res.WofFileListRes;
import com.isxcode.star.backend.module.user.action.UserLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Tag(name = "作业资源文件模块")
@RestController
@RequestMapping(ModulePrefix.WORK_FILE)
@RequiredArgsConstructor
public class WorkFileController {

  private final WorkFileBizService workFileBizService;

  private final WorkFileMapper workFileMapper;

  @UserLog
  @Operation(summary = "作业资源文件上传")
  @PostMapping("/fileUpload")
  @SuccessResponse("上传成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public WofAddFileRes fileUpload(
      @Schema(description = "资源文件", example = "xxx.jar") @RequestParam("file") MultipartFile file,
      @Schema(description = "资源文件类型", example = "jar") @RequestParam("type") String type,
      @Schema(description = "作业唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
          @RequestParam("workId")
          String workId)
      throws IOException {
    return workFileMapper.workFileEntityToWofAddFileRes(
        workFileBizService.fileUpload(file, type, workId));
  }

  @UserLog
  @Operation(summary = "作业资源文件下载")
  @GetMapping("/fileDownload/{fileId}")
  @SuccessResponse("下载成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public void fileDownload(
      @Schema(description = "资源文件唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
          @PathVariable("fileId")
          String fileId,
      HttpServletResponse response)
      throws IOException {
    workFileBizService.fileDownload(fileId, response);
  }

  @UserLog
  @Operation(summary = "资源文件删除")
  @GetMapping("/fileDelete/{fileId}")
  @SuccessResponse("删除成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public void fileDelete(
      @Schema(description = "资源文件唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
          @PathVariable("fileId")
          String fileId)
      throws IOException {
    workFileBizService.fileDelete(fileId);
  }

  @UserLog
  @Operation(summary = "资源文件查询")
  @PostMapping("/fileList")
  @SuccessResponse("查询成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public List<WofFileListRes> fileList(@Valid @RequestBody WofFileListReq wofFileListReq)
      throws IOException {
    return workFileMapper.workFileEntityListToWofFileListResList(
        workFileBizService.fileList(wofFileListReq));
  }
}
