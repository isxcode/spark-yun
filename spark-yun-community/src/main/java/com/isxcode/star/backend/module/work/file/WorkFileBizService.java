package com.isxcode.star.backend.module.work.file;

import com.isxcode.star.api.constants.work.WorkFileType;
import com.isxcode.star.api.exceptions.SparkYunException;
import com.isxcode.star.api.pojos.work.file.req.WofFileListReq;
import com.isxcode.star.api.properties.SparkYunProperties;
import com.isxcode.star.backend.module.work.WorkEntity;
import com.isxcode.star.backend.module.work.WorkRepository;
import com.isxcode.star.backend.module.work.config.WorkConfigEntity;
import com.isxcode.star.backend.module.work.config.WorkConfigRepository;
import com.isxcode.star.common.utils.PathUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** 作业模块文件接口的业务逻辑. */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WorkFileBizService {

  private final WorkConfigRepository workConfigRepository;

  private final WorkRepository workRepository;

  private final WorkFileRepository workFileRepository;

  private final SparkYunProperties sparkYunProperties;

  @Transactional(rollbackOn = Exception.class)
  public WorkFileEntity fileUpload(MultipartFile file, String type, String workId)
      throws IOException {

    if (WorkFileType.JAR.equals(type)) {

      List<WorkFileEntity> workFileEntities = workFileRepository.searchAllByWorkId(type, workId);
      if (workFileEntities.size() > 0) {
        throw new SparkYunException("作业文件已存在，请删除后重新提交。");
      }

      Optional<WorkEntity> workEntityOptional = workRepository.findById(workId);
      WorkConfigEntity workConfigEntity = getConfigFromWork(workEntityOptional);
      workConfigEntity.setJarName(file.getOriginalFilename());
      workConfigRepository.save(workConfigEntity);
    }

    // 文件存储路径
    String localPath =
        sparkYunProperties.getResourcesPath()
            + File.separator
            + workId
            + File.separator
            + type
            + File.separator
            + file.getOriginalFilename();

    File localFile = PathUtils.createFile(localPath);
    file.transferTo(localFile);

    return workFileRepository.save(
        WorkFileEntity.builder()
            .workId(workId)
            .fileName(file.getOriginalFilename())
            .fileType(type)
            .filePath(localPath)
            .fileSize(getDataSize(file.getSize()))
            .build());
  }

  public void fileDownload(String fileId, HttpServletResponse response) throws IOException {
    Optional<WorkFileEntity> workFileEntityOptional = workFileRepository.findById(fileId);
    if (!workFileEntityOptional.isPresent()) {
      throw new SparkYunException("文件不存在");
    }
    WorkFileEntity workFileEntity = workFileEntityOptional.get();
    InputStream inputStream = Files.newInputStream(Paths.get(workFileEntity.getFilePath()));

    response.setCharacterEncoding("utf-8");
    response.setHeader(
        "Content-Disposition",
        "attachment;filename=" + URLEncoder.encode(workFileEntity.getFileName(), "UTF-8"));
    IOUtils.copy(inputStream, response.getOutputStream());
  }

  @Transactional(rollbackOn = Exception.class)
  public void fileDelete(String fileId) throws IOException {

    Optional<WorkFileEntity> workFileEntityOptional = workFileRepository.findById(fileId);
    if (!workFileEntityOptional.isPresent()) {
      throw new SparkYunException("文件不存在");
    }
    WorkFileEntity workFileEntity = workFileEntityOptional.get();

    File localFile = PathUtils.createFile(workFileEntity.getFilePath());
    // 重命名文件
    workFileEntity.setFilePath(workFileEntity.getFilePath() + "." + new Date().getTime() + ".bak");
    workFileEntity.setFileName(workFileEntity.getFileName() + "." + new Date().getTime() + ".bak");
    localFile.renameTo(new File(workFileEntity.getFilePath()));
    // 更改资源文件数据状态
    workFileRepository.saveAndFlush(workFileEntity);
    workFileRepository.deleteById(fileId);

    // 变更作业配置信息
    Optional<WorkEntity> workEntityOptional = workRepository.findById(workFileEntity.getWorkId());
    WorkConfigEntity workConfigEntity = getConfigFromWork(workEntityOptional);
    workConfigEntity.setJarName(null);
    workConfigEntity.setMainClass(null);
    workConfigEntity.setArgs(null);
    workConfigRepository.save(workConfigEntity);
  }

  private WorkConfigEntity getConfigFromWork(Optional<WorkEntity> workEntityOptional) {
    if (!workEntityOptional.isPresent()) {
      throw new SparkYunException("作业不存在。");
    }

    Optional<WorkConfigEntity> workConfigEntityOptional =
        workConfigRepository.findById(workEntityOptional.get().getConfigId());
    if (!workConfigEntityOptional.isPresent()) {
      throw new SparkYunException("作业异常，作业不可用。");
    }
    return workConfigEntityOptional.get();
  }

  public List<WorkFileEntity> fileList(WofFileListReq wofFileListReq) throws IOException {
    return workFileRepository.searchAllByWorkId(
        wofFileListReq.getType(), wofFileListReq.getWorkId());
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
