package com.isxcode.star.modules.cluster.run;

import static com.isxcode.star.security.main.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.security.main.WebSecurityConfig.USER_ID;
import static com.isxcode.star.utils.ssh.SshUtils.executeCommand;
import static com.isxcode.star.utils.ssh.SshUtils.scpFile;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.api.constants.PathConstants;
import com.isxcode.star.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.star.api.cluster.pojos.dto.AgentInfo;
import com.isxcode.star.api.cluster.pojos.dto.ScpFileEngineNodeDto;
import com.isxcode.star.backend.api.base.exceptions.SparkYunException;
import com.isxcode.star.backend.api.base.properties.SparkYunProperties;
import com.isxcode.star.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.star.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.star.utils.path.PathUtils;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(noRollbackFor = {SparkYunException.class})
public class RunAgentInstallService {

  private final SparkYunProperties sparkYunProperties;

  private final ClusterNodeRepository clusterNodeRepository;

  @Async("sparkYunWorkThreadPool")
  public void run(
      String clusterNodeId,
      String clusterType,
      ScpFileEngineNodeDto scpFileEngineNodeDto,
      String tenantId,
      String userId) {

    USER_ID.set(userId);
    TENANT_ID.set(tenantId);

    // 获取节点信息
    Optional<ClusterNodeEntity> clusterNodeEntityOptional =
        clusterNodeRepository.findById(clusterNodeId);
    if (!clusterNodeEntityOptional.isPresent()) {
      return;
    }
    ClusterNodeEntity clusterNodeEntity = clusterNodeEntityOptional.get();

    try {
      installAgent(scpFileEngineNodeDto, clusterNodeEntity, clusterType);
    } catch (Exception e) {
      log.error(e.getMessage());
      clusterNodeEntity.setCheckDateTime(LocalDateTime.now());
      clusterNodeEntity.setAgentLog(e.getMessage());
      clusterNodeEntity.setStatus(ClusterNodeStatus.UN_INSTALL);
      clusterNodeRepository.saveAndFlush(clusterNodeEntity);
    }
  }

  public void installAgent(
      ScpFileEngineNodeDto scpFileEngineNodeDto, ClusterNodeEntity engineNode, String clusterType)
      throws JSchException, IOException, InterruptedException, SftpException {

    // 先检查节点是否可以安装
    scpFile(
        scpFileEngineNodeDto,
        PathUtils.parseProjectPath(sparkYunProperties.getAgentBinDir())
            + File.separator
            + "env"
            + File.separator
            + String.format(PathConstants.AGENT_ENV_BASH_FORMAT, clusterType),
        "/tmp/" + String.format(PathConstants.AGENT_ENV_BASH_FORMAT, clusterType));

    // 运行安装脚本
    String envCommand =
        "bash /tmp/"
            + String.format(PathConstants.AGENT_ENV_BASH_FORMAT, clusterType)
            + " --home-path="
            + engineNode.getAgentHomePath()
            + File.separator
            + PathConstants.AGENT_PATH_NAME
            + " --agent-port="
            + engineNode.getAgentPort();

    // 获取返回结果
    String executeLog = executeCommand(scpFileEngineNodeDto, envCommand, false);
    AgentInfo agentEnvInfo = JSON.parseObject(executeLog, AgentInfo.class);

    // 如果不可以安装直接返回
    if (!ClusterNodeStatus.CAN_INSTALL.equals(agentEnvInfo.getStatus())) {
      engineNode.setStatus(agentEnvInfo.getStatus());
      engineNode.setAgentLog(agentEnvInfo.getLog());
      engineNode.setCheckDateTime(LocalDateTime.now());
      clusterNodeRepository.saveAndFlush(engineNode);
      return;
    }

    // 下载安装包
    scpFile(
        scpFileEngineNodeDto,
        PathUtils.parseProjectPath(sparkYunProperties.getAgentTarGzDir())
            + File.separator
            + PathConstants.SPARK_YUN_AGENT_TAR_GZ_NAME,
        "/tmp/" + PathConstants.SPARK_YUN_AGENT_TAR_GZ_NAME);

    // 拷贝安装脚本
    scpFile(
        scpFileEngineNodeDto,
        PathUtils.parseProjectPath(sparkYunProperties.getAgentBinDir())
            + File.separator
            + PathConstants.AGENT_INSTALL_BASH_NAME,
        "/tmp/" + PathConstants.AGENT_INSTALL_BASH_NAME);

    // 运行安装脚本
    String installCommand =
        "bash /tmp/"
            + PathConstants.AGENT_INSTALL_BASH_NAME
            + " --home-path="
            + engineNode.getAgentHomePath()
            + File.separator
            + PathConstants.AGENT_PATH_NAME
            + " --agent-port="
            + engineNode.getAgentPort();

    executeLog = executeCommand(scpFileEngineNodeDto, installCommand, false);
    AgentInfo agentInstallInfo = JSON.parseObject(executeLog, AgentInfo.class);

    if (ClusterNodeStatus.RUNNING.equals(agentInstallInfo.getStatus())) {
      engineNode.setStatus(agentInstallInfo.getStatus());
      engineNode.setAgentLog("安装成功");
      engineNode.setCheckDateTime(LocalDateTime.now());
      clusterNodeRepository.saveAndFlush(engineNode);
    }
  }
}
