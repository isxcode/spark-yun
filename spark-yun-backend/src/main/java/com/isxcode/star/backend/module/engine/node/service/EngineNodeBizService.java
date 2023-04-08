package com.isxcode.star.backend.module.engine.node.service;

import static com.isxcode.star.backend.utils.SshUtils.executeCommand;
import static com.isxcode.star.backend.utils.SshUtils.scpFile;

import com.isxcode.star.api.constants.PathConstants;
import com.isxcode.star.api.pojos.engine.node.req.EnoAddNodeReq;
import com.isxcode.star.api.pojos.engine.node.req.EnoQueryNodeReq;
import com.isxcode.star.api.pojos.engine.node.res.EnoCheckAgentRes;
import com.isxcode.star.api.pojos.engine.node.res.EnoInstallAgentRes;
import com.isxcode.star.api.pojos.engine.node.res.EnoQueryNodeRes;
import com.isxcode.star.api.pojos.engine.node.res.EnoRemoveAgentRes;
import com.isxcode.star.api.properties.SparkYunProperties;
import com.isxcode.star.backend.module.calculate.engine.entity.CalculateEngineEntity;
import com.isxcode.star.backend.module.calculate.engine.repository.CalculateEngineRepository;
import com.isxcode.star.backend.module.engine.node.entity.EngineNodeEntity;
import com.isxcode.star.backend.module.engine.node.mapper.EngineNodeMapper;
import com.isxcode.star.backend.module.engine.node.repository.EngineNodeRepository;
import com.isxcode.star.common.exception.SparkYunException;
import java.io.File;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/** 用户模块接口的业务逻辑. */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EngineNodeBizService {

  private final EngineNodeRepository engineNodeRepository;

  private final CalculateEngineRepository calculateEngineRepository;

  private final EngineNodeMapper engineNodeMapper;

  private final SparkYunProperties sparkYunProperties;

  public void addNode(EnoAddNodeReq enoAddNodeReq) {

    // 检查计算引擎是否存在
    Optional<CalculateEngineEntity> calculateEngineEntityOptional = calculateEngineRepository.findById(enoAddNodeReq.getCalculateEngineId());
    if (!calculateEngineEntityOptional.isPresent()) {
      throw new SparkYunException("计算引擎不存在");
    }

    EngineNodeEntity node = engineNodeMapper.addNodeReqToNodeEntity(enoAddNodeReq);

    // 设置安装地址
    node.setAgentHomePath(
      getDefaultAgentHomePath(enoAddNodeReq.getAgentHomePath(), enoAddNodeReq.getUsername()));

    // 设置代理端口号
    if (Strings.isEmpty(enoAddNodeReq.getAgentPort())) {
      node.setAgentPort(sparkYunProperties.getDefaultAgentPort());
    }

    engineNodeRepository.save(node);
  }

  public String getDefaultAgentHomePath(String agentHomePath, String username) {

    if (Strings.isEmpty(agentHomePath)) {

      if ("root".equals(username)) {
        return "/root/";
      } else {
        return "/home/" + username + "/";
      }
    } else {
      return agentHomePath;
    }
  }

  public Page<EnoQueryNodeRes> queryNodes(EnoQueryNodeReq enoQueryNodeReq) {

    Page<EngineNodeEntity> engineNodeEntities =
        engineNodeRepository.findAllByCalculateEngineId(
            enoQueryNodeReq.getCalculateEngineId(),
            PageRequest.of(enoQueryNodeReq.getPage(), enoQueryNodeReq.getPageSize()));

    return engineNodeMapper.datasourceEntityPageToQueryDatasourceResPage(engineNodeEntities);
  }

  public void delNode(String nodeId) {

    engineNodeRepository.deleteById(nodeId);
  }

  public EngineNodeEntity getEngineNode(String engineNodeId) {

    Optional<EngineNodeEntity> engineNodeEntityOptional =
        engineNodeRepository.findById(engineNodeId);

    if (!engineNodeEntityOptional.isPresent()) {
      throw new SparkYunException("节点不存在");
    }

    return engineNodeEntityOptional.get();
  }

  /** 安装节点. */
  public EnoInstallAgentRes installAgent(String engineNodeId) {

    EngineNodeEntity engineNode = getEngineNode(engineNodeId);

    // 拷贝安装包
    scpFile(
      engineNode,
      sparkYunProperties.getAgentTarGzDir()
        + File.separator
        + PathConstants.SPARK_YUN_AGENT_TAR_GZ_NAME,
      engineNode.getAgentHomePath() + File.separator + PathConstants.SPARK_YUN_AGENT_TAR_GZ_NAME);

    // 拷贝执行脚本
    scpFile(
      engineNode,
      sparkYunProperties.getAgentBinDir()
        + File.separator
        + PathConstants.AGENT_INSTALL_BASH_NAME,
      engineNode.getAgentHomePath() + File.separator + PathConstants.AGENT_INSTALL_BASH_NAME);

    // 运行安装脚本
    String installCommand =
      "bash " + engineNode.getAgentHomePath() + File.separator + PathConstants.AGENT_INSTALL_BASH_NAME + " --home-path=" + engineNode.getAgentHomePath() + " --agent-port=" + engineNode.getAgentPort();
    String executeLog = executeCommand(engineNode, installCommand, false);

    engineNode.setStatus("INSTALLED");
    engineNodeRepository.save(engineNode);

    return new EnoInstallAgentRes(executeLog);
  }

  public EnoCheckAgentRes checkAgent(String engineNodeId) {

    EngineNodeEntity engineNode = getEngineNode(engineNodeId);

    // 运行卸载脚本
    String checkCommand = "echo \"可安装\"";
    String executeLog = executeCommand(engineNode, checkCommand, false);

    engineNode.setStatus("CAN_INSTALLED");
    engineNodeRepository.save(engineNode);

    return new EnoCheckAgentRes(executeLog);
  }

  public EnoRemoveAgentRes removeAgent(String engineNodeId) {

    EngineNodeEntity engineNode = getEngineNode(engineNodeId);

    // 运行卸载脚本
    String installCommand =
      "bash " + engineNode.getAgentHomePath() + File.separator + "spark-yun-agent" + File.separator + "bin" + File.separator + PathConstants.AGENT_REMOVE_BASH_NAME + " --home-path=" + engineNode.getAgentHomePath();
    String executeLog = executeCommand(engineNode, installCommand, false);

    engineNode.setStatus("UNINSTALLED");
    engineNodeRepository.save(engineNode);

    return new EnoRemoveAgentRes(executeLog);
  }
}
