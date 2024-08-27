package com.isxcode.star.modules.file.service;

import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.file.entity.FileEntity;
import com.isxcode.star.modules.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    public FileEntity getFile(String fileId) {

        return fileRepository.findById(fileId).orElseThrow(() -> new IsxAppException("资源不存在"));
    }

    public String getFileName(String fileId) {

        FileEntity fileEntity = fileRepository.findById(fileId).orElse(null);
        return fileEntity == null ? fileId : fileEntity.getFileName();
    }
}
