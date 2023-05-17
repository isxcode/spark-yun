package com.isxcode.star.backend.module.cluster.node.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.constants.EngineNodeStatus;
import com.isxcode.star.api.constants.PathConstants;
import com.isxcode.star.api.exception.SparkYunException;
import com.isxcode.star.api.pojos.engine.node.dto.AgentCheckInfo;
import com.isxcode.star.api.pojos.engine.node.dto.ScpFileEngineNodeDto;
import com.isxcode.star.api.properties.SparkYunProperties;
import com.isxcode.star.backend.module.cluster.node.entity.ClusterNodeEntity;
import com.isxcode.star.backend.module.cluster.node.repository.ClusterNodeRepository;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.isxcode.star.api.utils.SshUtils.executeCommand;
import static com.isxcode.star.api.utils.SshUtils.scpFile;
import static com.isxcode.star.backend.config.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.backend.config.WebSecurityConfig.USER_ID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(noRollbackFor = {SparkYunException.class})
public class RunAgentCheckService {

  private final SparkYunProperties sparkYunProperties;

  private final ClusterNodeRepository clusterNodeRepository;

  @Async("sparkYunWorkThreadPool")
  public void run(String clusterNodeId, ScpFileEngineNodeDto scpFileEngineNodeDto, String tenantId, String userId) {

    USER_ID.set(userId);
    TENANT_ID.set(tenantId);

    // 获取节点信息
    Optional<ClusterNodeEntity> clusterNodeEntityOptional = clusterNodeRepository.findById(clusterNodeId);
    if (!clusterNodeEntityOptional.isPresent()) {
      return;
    }
    ClusterNodeEntity clusterNodeEntity = clusterNodeEntityOptional.get();

    try {
      checkAgent(scpFileEngineNodeDto, clusterNodeEntity);
    } catch (Exception e) {
      log.error(e.getMessage());
      clusterNodeEntity.setCheckDateTime(LocalDateTime.now());
      clusterNodeEntity.setAgentLog(e.getMessage());
      clusterNodeEntity.setStatus(EngineNodeStatus.CHECK_ERROR);
      clusterNodeRepository.saveAndFlush(clusterNodeEntity);
    }
  }

  public void checkAgent(ScpFileEngineNodeDto scpFileEngineNodeDto, ClusterNodeEntity engineNode) throws JSchException, IOException, InterruptedException, SftpException {

    // 拷贝检测脚本
    scpFile(
      scpFileEngineNodeDto,
      sparkYunProperties.getAgentBinDir() + File.separator + PathConstants.AGENT_CHECK_BASH_NAME,
      "/tmp/" + PathConstants.AGENT_CHECK_BASH_NAME);

    // 运行安装脚本
    String checkCommand =
      "bash /tmp/" + PathConstants.AGENT_CHECK_BASH_NAME + " --home-path=" + engineNode.getAgentHomePath() + File.separator + PathConstants.AGENT_PATH_NAME;

    // 获取返回结果
    String executeLog = executeCommand(scpFileEngineNodeDto, checkCommand, false);
    AgentCheckInfo agentCheckInfo = JSON.parseObject(executeLog, AgentCheckInfo.class);

    // 保存服务器信息
    engineNode.setAllMemory(agentCheckInfo.getAllMemory());
    engineNode.setUsedMemory(agentCheckInfo.getUsedMemory());
    engineNode.setAllStorage(agentCheckInfo.getAllStorage());
    engineNode.setUsedStorage(agentCheckInfo.getUsedStorage());
    engineNode.setCpuPercent(agentCheckInfo.getCpuPercent());

    // 修改状态
    engineNode.setStatus(agentCheckInfo.getCheckStatus());
    engineNode.setAgentLog(agentCheckInfo.getLog());
    engineNode.setCheckDateTime(LocalDateTime.now());
    clusterNodeRepository.saveAndFlush(engineNode);
  }
}
