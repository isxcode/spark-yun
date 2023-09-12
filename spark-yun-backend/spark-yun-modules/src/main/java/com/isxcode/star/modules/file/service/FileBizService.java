package com.isxcode.star.modules.file.service;


import com.isxcode.star.api.file.pojos.req.FileListReq;
import com.isxcode.star.api.main.properties.SparkYunProperties;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.common.utils.path.PathUtils;
import com.isxcode.star.modules.file.entity.FileEntity;
import com.isxcode.star.modules.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
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
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/** 资源文件接口的业务逻辑. */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FileBizService {

  private final FileRepository fileRepository;

  private final SparkYunProperties sparkYunProperties;

  @Transactional(rollbackOn = Exception.class)
  public FileEntity fileUpload(MultipartFile file, String type)
      throws IOException {

    // 文件存储路径
    String localPath =
      sparkYunProperties.getResourceDir()
            + File.separator
            + type
            + File.separator
            + file.getOriginalFilename();
    File folder = PathUtils.createFile(localPath);
    File dest = new File(folder.getAbsolutePath());

    file.transferTo(dest);

    return fileRepository.save(
        FileEntity.builder()
            .fileName(file.getOriginalFilename())
            .fileType(type)
            .filePath(localPath)
            .fileSize(getDataSize(file.getSize()))
            .build());
  }

  public void fileDownload(String fileId, HttpServletResponse response) throws IOException {
    Optional<FileEntity> fileEntityOptional = fileRepository.findById(fileId);
    if (!fileEntityOptional.isPresent()) {
      throw new IsxAppException("文件不存在");
    }
    FileEntity fileEntity = fileEntityOptional.get();
    InputStream inputStream = Files.newInputStream(Paths.get(fileEntity.getFilePath()));

    response.setCharacterEncoding("utf-8");
    response.setHeader(
        "Content-Disposition",
        "attachment;filename=" + URLEncoder.encode(fileEntity.getFileName(), "UTF-8"));
    IOUtils.copy(inputStream, response.getOutputStream());
  }


  @Transactional(rollbackOn = Exception.class)
  public void fileDelete(String fileId) throws IOException {

    Optional<FileEntity> fileEntityOptional = fileRepository.findById(fileId);
    if (!fileEntityOptional.isPresent()) {
      throw new IsxAppException("文件不存在");
    }
    FileEntity workFileEntity = fileEntityOptional.get();

    File localFile = PathUtils.createFile(workFileEntity.getFilePath());
    // 重命名文件
    workFileEntity.setFilePath(workFileEntity.getFilePath() + "." + new Date().getTime() + ".bak");
    workFileEntity.setFileName(workFileEntity.getFileName() + "." + new Date().getTime() + ".bak");
    localFile.renameTo(new File(workFileEntity.getFilePath()));
    // 更改资源文件数据状态
    fileRepository.saveAndFlush(workFileEntity);
    fileRepository.deleteById(fileId);

  }


  public List<FileEntity> fileList(FileListReq fileListReq) {
    return fileRepository.findByFileTypeAndFileName(fileListReq.getType(),fileListReq.getFileName());
  }


  public static String getDataSize(long size) {
    DecimalFormat formatter = new DecimalFormat("####.00");
    float countSize;
    if (size < 1024) {
      return size + "bytes";
    } else if (size < 1024 * 1024) {
      countSize = size / 1024f;
      return formatter.format(countSize) + "KB";
    } else if (size < 1024 * 1024 * 1024) {
      countSize = size / 1024f / 1024f;
      return formatter.format(countSize) + "MB";
    } else if (size < 1024L * 1024 * 1024 * 1024) {
      countSize = size / 1024f / 1024f / 1024f;
      return formatter.format(countSize) + "GB";
    } else {
      return "size: error";
    }
  }
}
