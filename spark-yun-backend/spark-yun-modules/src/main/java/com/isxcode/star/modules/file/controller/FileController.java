package com.isxcode.star.modules.file.controller;

import com.isxcode.star.api.file.pojos.req.FileDeleteReq;
import com.isxcode.star.api.file.pojos.req.FileDownloadReq;
import com.isxcode.star.api.file.pojos.req.FileListReq;
import com.isxcode.star.api.file.pojos.res.AddFileRes;
import com.isxcode.star.api.file.pojos.res.FileListRes;
import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.file.mapper.FileMapper;
import com.isxcode.star.modules.file.service.FileBizService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping(ModuleCode.FILE)
@RequiredArgsConstructor
public class FileController {

	private final FileBizService fileBizService;

	private final FileMapper fileMapper;

	@Operation(summary = "资源文件上传")
	@PostMapping("/fileUpload")
	@SuccessResponse("上传成功")
	public AddFileRes fileUpload(@RequestParam("file") MultipartFile file, @RequestParam("type") String type)
			throws IOException {
		return fileMapper.fileEntityToAddFileRes(fileBizService.fileUpload(file, type));
	}

	@Operation(summary = "资源文件下载")
	@PostMapping("/fileDownload")
	@SuccessResponse("下载成功")
	public void fileDownload(@Valid @RequestBody FileDownloadReq fileDownloadReq, HttpServletResponse response)
			throws IOException {
		fileBizService.fileDownload(fileDownloadReq.getFileId(), response);
	}

	@Operation(summary = "资源文件删除")
	@PostMapping("/fileDelete")
	@SuccessResponse("删除成功")
	public void fileDelete(@Valid @RequestBody FileDeleteReq fileDeleteReq) throws IOException {
		fileBizService.fileDelete(fileDeleteReq.getFileId());
	}

	@Operation(summary = "资源文件查询")
	@PostMapping("/fileList")
	@SuccessResponse("查询成功")
	public List<FileListRes> fileList(@Valid @RequestBody FileListReq fileListReq) {
		return fileMapper.fileEntityListToFileListResList(fileBizService.fileList(fileListReq));
	}
}
