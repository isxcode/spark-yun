package com.isxcode.star.modules.cluster.service;

import com.isxcode.star.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.star.api.main.properties.SparkYunProperties;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.common.utils.aes.AesUtils;
import com.isxcode.star.common.utils.ssh.SshUtils;
import com.isxcode.star.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.star.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.star.modules.cluster.repository.ClusterNodeRepository;
import com.jcraft.jsch.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.isxcode.star.common.utils.ssh.SshUtils.scpFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClusterNodeService {

    private final SparkYunProperties sparkYunProperties;

    private final ClusterNodeRepository clusterNodeRepository;

    private final ClusterNodeMapper clusterNodeMapper;

    private final AesUtils aesUtils;

    public String getDefaultAgentHomePath(String username, ClusterNodeEntity clusterNode) {

        ScpFileEngineNodeDto scpFileEngineNodeDto =
            clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(clusterNode);
        scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));
        String getOsType = "echo $OSTYPE";
        try {
            String systemType = SshUtils.executeCommand(scpFileEngineNodeDto, getOsType, false);
            if (systemType.contains("darwin")) {
                return "/Users/" + username;
            }
            if (systemType.contains("linux")) {
                if ("root".equals(username)) {
                    return "/root";
                } else {
                    return "/home/" + username;
                }
            }
            throw new IsxAppException("不支持该节点服务器系统");
        } catch (JSchException | InterruptedException | IOException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException("无法获取节点信息");
        }
    }

    public String getDefaultAgentPort(String agentPort) {

        if (Strings.isEmpty(agentPort)) {
            return sparkYunProperties.getDefaultAgentPort();
        } else {
            return agentPort;
        }
    }

    public ClusterNodeEntity getClusterNode(String clusterNodeId) {

        return clusterNodeRepository.findById(clusterNodeId).orElseThrow(() -> new IsxAppException("节点不存在"));
    }

    public void checkScpPercent(ScpFileEngineNodeDto engineNode, String srcPath, String dstPath,
        ClusterNodeEntity clusterNode) throws JSchException, IOException, InterruptedException {

        // 初始化jsch
        JSch jsch = new JSch();

        if (engineNode.getPasswd().length() > 1000) {
            jsch.addIdentity(engineNode.getUsername(), engineNode.getPasswd().getBytes(), null, null);
        }

        Session session =
            jsch.getSession(engineNode.getUsername(), engineNode.getHost(), Integer.parseInt(engineNode.getPort()));

        // 连接远程服务器
        if (engineNode.getPasswd().length() < 1000) {
            session.setPassword(engineNode.getPasswd());
        }

        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        // 初始化sftp功能
        ChannelSftp channel;
        channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect(120000);
        FileSystemResourceLoader resourceLoader = new FileSystemResourceLoader();

        // 文件校验
        SftpATTRS attrs;

        int scpPercent = 0;
        while (scpPercent < 100) {

            try {
                attrs = channel.stat(dstPath);
            } catch (Exception e) {
                log.debug("上传文件不存在，继续获取文件，不影响安装");
                continue;
            }

            if (attrs != null) {
                long remoteFileSize = attrs.getSize();
                long localFileSize = resourceLoader.getResource(srcPath).contentLength();
                scpPercent = (int) (remoteFileSize * 100 / localFileSize);
            }

            clusterNode = clusterNodeRepository.findById(clusterNode.getId()).get();
            clusterNode.setAgentLog(clusterNode.getAgentLog() + "\n进度:" + scpPercent + "%");
            clusterNodeRepository.saveAndFlush(clusterNode);

            Thread.sleep(10000);
        }

        channel.disconnect();
        session.disconnect();
    }

    @Async("sparkYunThreadPool")
    public void scpAgentFile(ScpFileEngineNodeDto clusterNode, String srcPath, String dstPath)
        throws JSchException, SftpException, IOException, InterruptedException {

        scpFile(clusterNode, srcPath, dstPath);
    }
}
