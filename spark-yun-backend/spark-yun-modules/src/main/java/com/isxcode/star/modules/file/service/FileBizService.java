package com.isxcode.star.modules.file.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.unit.DataSizeUtil;
import com.isxcode.star.api.file.req.DeleteFileReq;
import com.isxcode.star.api.file.req.DownloadFileReq;
import com.isxcode.star.api.file.req.PageFileReq;
import com.isxcode.star.api.file.res.PageFileRes;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import com.isxcode.star.common.utils.path.PathUtils;
import com.isxcode.star.modules.file.entity.FileEntity;
import com.isxcode.star.modules.file.mapper.FileMapper;
import com.isxcode.star.modules.file.repository.FileRepository;
import com.isxcode.star.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static com.isxcode.star.common.config.CommonConfig.TENANT_ID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FileBizService {

    private final FileRepository fileRepository;

    private final FileService fileService;

    private final IsxAppProperties isxAppProperties;

    private final FileMapper fileMapper;
    private final UserService userService;

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
                log.debug(e.getMessage(), e);
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
            log.debug(e.getMessage(), e);
            throw new IsxAppException("上传资源文件失败");
        }
    }

    public void updateFile(String fileId, MultipartFile file, String remark) {

        // 校验文件是否存在
        FileEntity fileEntity = fileService.getFile(fileId);

        // 修改备注
        fileEntity.setRemark(remark);

        if (file != null) {

            // 判断文件是否重复
            if (!fileEntity.getFileName().equals(file.getOriginalFilename())) {
                Optional<FileEntity> fileByNameOptional = fileRepository.findByFileName(file.getOriginalFilename());
                if (fileByNameOptional.isPresent()) {
                    throw new IsxAppException("文件已重复存在");
                }
            }

            // 将原有的文件，加一个update_${timestamp}的后缀
            String fileDir = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "file"
                + File.separator + TENANT_ID.get();
            try {
                File localFile = PathUtils.createFile(fileDir + File.separator + fileEntity.getId());
                localFile
                    .renameTo(new File(fileDir + File.separator + fileEntity.getId() + ".updated_" + DateUtil.now()));
            } catch (IOException e) {
                throw new IsxAppException("本地文件无法获取");
            }

            // 持久化文件
            String filePath = fileDir + File.separator + fileEntity.getId();
            try {
                File folder = PathUtils.createFile(filePath);
                File dest = new File(folder.getAbsolutePath());
                file.transferTo(dest);
            } catch (IOException e) {
                throw new IsxAppException("上传资源文件失败");
            }

            fileEntity.setFileName(file.getOriginalFilename());
            fileEntity.setFileSize(DataSizeUtil.format(file.getSize()));
        }

        // 数据持久化
        fileRepository.save(fileEntity);
    }

    public ResponseEntity<Resource> downloadFile(DownloadFileReq downloadFileReq) {

        // 获取文件信息
        FileEntity file = fileService.getFile(downloadFileReq.getFileId());
        String fileDir = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "file"
            + File.separator + TENANT_ID.get();

        try {
            InputStreamResource resource =
                new InputStreamResource(Files.newInputStream(Paths.get(fileDir + File.separator + file.getId())));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", URLEncoder.encode(file.getFileName(), "UTF-8"));

            // 返回文件
            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (IOException e) {
            log.debug(e.getMessage(), e);
            throw new IsxAppException("读取文件失败");
        }
    }

    public void deleteFile(DeleteFileReq deleteFileReq) {

        // 获取文件内容
        FileEntity file = fileService.getFile(deleteFileReq.getFileId());

        // 将原有的文件，加一个deleted的后缀
        String fileDir = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "file"
            + File.separator + TENANT_ID.get();
        try {
            File localFile = PathUtils.createFile(fileDir + File.separator + file.getId());
            localFile.renameTo(new File(fileDir + File.separator + file.getId() + ".deleted"));
        } catch (IOException e) {
            log.debug(e.getMessage(), e);
            throw new IsxAppException("本地文件无法获取");
        }

        // 数据持久化
        fileRepository.delete(file);
    }

    public Page<PageFileRes> pageFile(PageFileReq pageFileReq) {

        Page<FileEntity> fileEntitiePage = fileRepository.searchAll(pageFileReq.getSearchKeyWord(),
            pageFileReq.getType(), PageRequest.of(pageFileReq.getPage(), pageFileReq.getPageSize()));

        Page<PageFileRes> map = fileEntitiePage.map(fileMapper::fileEntityToPageFileRes);

        map.getContent().forEach(e -> e.setCreateUsername(userService.getUserName(e.getCreateBy())));

        return map;
    }
}
