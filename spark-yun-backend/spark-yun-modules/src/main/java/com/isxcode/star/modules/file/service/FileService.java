package com.isxcode.star.modules.file.service;

import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.file.entity.FileEntity;
import com.isxcode.star.modules.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 资源文件接口的业务逻辑.
 */
@Service
@RequiredArgsConstructor
public class FileService {

	private final FileRepository fileRepository;

	public FileEntity getFile(String fileId) {

		return fileRepository.findById(fileId).orElseThrow(() -> new IsxAppException("资源不存在"));
	}
}
