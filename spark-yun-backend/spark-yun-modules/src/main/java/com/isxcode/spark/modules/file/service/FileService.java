package com.isxcode.spark.modules.file.service;

import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.modules.file.entity.FileEntity;
import com.isxcode.spark.modules.file.entity.LibPackageEntity;
import com.isxcode.spark.modules.file.repository.FileRepository;
import com.isxcode.spark.modules.file.repository.LibPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    private final LibPackageRepository libPackageRepository;

    public FileEntity getFile(String fileId) {

        return fileRepository.findById(fileId).orElseThrow(() -> new IsxAppException("资源不存在"));
    }

    public LibPackageEntity getLibPackage(String libPackageId) {

        return libPackageRepository.findById(libPackageId).orElseThrow(() -> new IsxAppException("依赖包不存在"));
    }

    public String getFileName(String fileId) {

        FileEntity fileEntity = fileRepository.findById(fileId).orElse(null);
        return fileEntity == null ? fileId : fileEntity.getFileName();
    }
}
