package com.isxcode.star.modules.file.service;

import cn.hutool.core.io.unit.DataSizeUtil;
import com.isxcode.star.api.file.pojos.req.DeleteFileReq;
import com.isxcode.star.api.file.pojos.req.DownloadFileReq;
import com.isxcode.star.api.file.pojos.req.PageFileReq;
import com.isxcode.star.api.file.pojos.res.PageFileRes;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import com.isxcode.star.common.utils.path.PathUtils;
import com.isxcode.star.modules.file.entity.FileEntity;
import com.isxcode.star.modules.file.mapper.FileMapper;
import com.isxcode.star.modules.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static com.isxcode.star.common.config.CommonConfig.TENANT_ID;

/**
 * 资源文件接口的业务逻辑.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FileBizService {

	private final FileRepository fileRepository;

	private final FileService fileService;

	private final IsxAppProperties isxAppProperties;

	private final FileMapper fileMapper;

	public void uploadFile(MultipartFile file, String type, String remark) {

		// 判断文件是否重复
		Optional<FileEntity> fileByNameOptional = fileRepository.findByFileName(file.getOriginalFilename());
		if (fileByNameOptional.isPresent()) {
			throw new IsxAppException("文件已重复存在");
		}

		// 判断文件夹是否存在，不存在则创建
		String fileDir = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "file"
				+ File.separator + TENANT_ID.get();
		if (!new File(fileDir).exists()) {
			try {
				Files.createDirectories(Paths.get(fileDir));
			} catch (IOException e) {
				throw new IsxAppException("上传资源文件，目录创建失败");
			}
		}

		// 持久化数据
		FileEntity fileEntity = FileEntity.builder().fileName(file.getOriginalFilename()).fileType(type)
				.fileSize(DataSizeUtil.format(file.getSize())).remark(remark).build();
		fileEntity = fileRepository.save(fileEntity);

		// 持久化文件
		String filePath = fileDir + File.separator + fileEntity.getId();
		try {
			File folder = PathUtils.createFile(filePath);
			File dest = new File(folder.getAbsolutePath());
			file.transferTo(dest);
		} catch (IOException e) {
			throw new IsxAppException("上传资源文件失败");
		}
	}

	public void downloadFile(DownloadFileReq downloadFileReq, HttpServletResponse response) {

		// 获取文件信息
		FileEntity file = fileService.getFile(downloadFileReq.getFileId());
		String fileDir = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "file"
				+ File.separator + TENANT_ID.get();

		try {
			InputStream inputStream = Files.newInputStream(Paths.get(fileDir + File.separator + file.getId()));
			response.setCharacterEncoding("utf-8");
			response.setHeader("Content-Disposition",
					"attachment;filename=" + URLEncoder.encode(file.getFileName(), "UTF-8"));
			IOUtils.copy(inputStream, response.getOutputStream());
		} catch (IOException e) {
			throw new IsxAppException("读取文件失败");
		}
	}

	public void deleteFile(DeleteFileReq deleteFileReq) {

		// 获取文件内容
		FileEntity file = fileService.getFile(deleteFileReq.getFileId());

		// 数据持久化
		fileRepository.delete(file);
	}

	public Page<PageFileRes> pageFile(PageFileReq pageFileReq) {

		Page<FileEntity> fileEntitiePage = fileRepository.searchAll(pageFileReq.getSearchKeyWord(),
				pageFileReq.getType(), PageRequest.of(pageFileReq.getPage(), pageFileReq.getPageSize()));

		return fileEntitiePage.map(fileMapper::fileEntityToPageFileRes);
	}
}
