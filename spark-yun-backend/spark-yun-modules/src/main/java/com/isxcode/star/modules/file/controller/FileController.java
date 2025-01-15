package com.isxcode.star.modules.file.controller;

import com.isxcode.star.api.file.req.DeleteFileReq;
import com.isxcode.star.api.file.req.DownloadFileReq;
import com.isxcode.star.api.file.req.PageFileReq;
import com.isxcode.star.api.file.res.PageFileRes;
import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.api.user.constants.RoleType;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.file.service.FileBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@Tag(name = "资源文件中心模块")
@RequestMapping(ModuleCode.FILE)
@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileBizService fileBizService;

    @Operation(summary = "资源文件上传接口")
    @PostMapping("/uploadFile")
    @SuccessResponse("上传成功")
    public void uploadFile(@RequestParam("file") @Schema(title = "文件") MultipartFile file,
        @RequestParam("type") @Schema(title = "文件类型")
        @Pattern(regexp = "^(JOB|FUNC|LIB|EXCEL)$", message = "只能是JOB/FUNC/LIB/EXCEL其中一个") String type,
        @RequestParam(value = "remark", required = false) @Schema(title = "备注") String remark) {

        fileBizService.uploadFile(file, type, remark);
    }

    @Operation(summary = "资源文件更新接口")
    @PostMapping("/updateFile")
    @SuccessResponse("更新成功")
    public void updateFile(@RequestParam("fileId") @Schema(title = "资源文件id") String fileId,
        @RequestParam(value = "file", required = false) @Schema(title = "文件") MultipartFile file,
        @RequestParam(value = "remark", required = false) @Schema(title = "备注") String remark) {

        fileBizService.updateFile(fileId, file, remark);
    }

    @Operation(summary = "资源文件下载接口")
    @PostMapping("/downloadFile")
    public ResponseEntity<Resource> downloadFile(@Valid @RequestBody DownloadFileReq downloadFileReq) {

        return fileBizService.downloadFile(downloadFileReq);
    }

    @Secured({RoleType.TENANT_ADMIN})
    @Operation(summary = "资源文件删除接口")
    @PostMapping("/deleteFile")
    @SuccessResponse("删除成功")
    public void deleteFile(@Valid @RequestBody DeleteFileReq deleteFileReq) {

        fileBizService.deleteFile(deleteFileReq);
    }

    @Operation(summary = "资源文件查询接口")
    @PostMapping("/pageFile")
    @SuccessResponse("查询成功")
    public Page<PageFileRes> pageFile(@Valid @RequestBody PageFileReq pageFileReq) {

        return fileBizService.pageFile(pageFileReq);
    }
}
