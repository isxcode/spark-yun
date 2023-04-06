package com.isxcode.star.backend.module.engine.node.service;

import static com.isxcode.star.backend.utils.SshUtils.executeCommand;
import static com.isxcode.star.backend.utils.SshUtils.scpFile;

import com.isxcode.star.api.constants.PathConstants;
import com.isxcode.star.api.pojos.engine.node.req.EnoAddNodeReq;
import com.isxcode.star.api.pojos.engine.node.req.EnoQueryNodeReq;
import com.isxcode.star.api.pojos.engine.node.res.EnoQueryNodeRes;
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

  private final EngineNodeMapper engineNodeMapper;

  public void addNode(EnoAddNodeReq enoAddNodeReq) {

    EngineNodeEntity node = engineNodeMapper.addNodeReqToNodeEntity(enoAddNodeReq);

    // 设置安装地址
    node.setHomePath(
        getDefaultAgentHomePath(enoAddNodeReq.getHomePath(), enoAddNodeReq.getUsername()));

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
        engineNodeRepository.findAllByEngineId(
            enoQueryNodeReq.getEngineId(),
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
  public void installAgent(String engineNodeId) {

    EngineNodeEntity engineNode = getEngineNode(engineNodeId);

    // 拷贝安装包
    scpFile(
        engineNode,
        PathConstants.DOCKER_SPARK_YUN_DIR
            + File.separator
            + PathConstants.SPARK_YUN_AGENT_TAR_GZ_NAME,
        engineNode.getHomePath() + File.separator + PathConstants.SPARK_YUN_AGENT_TAR_GZ_NAME);

    // 拷贝执行脚本
    scpFile(
        engineNode,
        PathConstants.DOCKER_SPARK_YUN_DIR
            + File.separator
            + "bin"
            + File.separator
            + PathConstants.AGENT_INSTALL_BASH_NAME,
        engineNode.getHomePath() + File.separator + PathConstants.AGENT_INSTALL_BASH_NAME);

    // 运行安装脚本
    String installCommand =
        "bash " + engineNode.getHomePath() + File.separator + PathConstants.AGENT_INSTALL_BASH_NAME;
    executeCommand(engineNode, installCommand, false);

    engineNode.setStatus("INSTALLED");
    engineNodeRepository.save(engineNode);
  }

  public void checkAgent(String engineNodeId) {

    EngineNodeEntity engineNode = getEngineNode(engineNodeId);

    // 运行卸载脚本
    String lsCommand = "ls";
    executeCommand(engineNode, lsCommand, false);

    engineNode.setStatus("CAN_INSTALLED");
    engineNodeRepository.save(engineNode);
  }

  public void removeAgent(String engineNodeId) {

    EngineNodeEntity engineNode = getEngineNode(engineNodeId);

    // 拷贝卸载脚本
    scpFile(
        engineNode,
        PathConstants.DOCKER_SPARK_YUN_DIR
            + File.separator
            + "bin"
            + File.separator
            + PathConstants.AGENT_REMOVE_BASH_NAME,
        engineNode.getHomePath() + File.separator + PathConstants.AGENT_REMOVE_BASH_NAME);

    // 运行卸载脚本s
    String installCommand =
        "bash " + engineNode.getHomePath() + File.separator + PathConstants.AGENT_REMOVE_BASH_NAME;
    executeCommand(engineNode, installCommand, false);

    engineNode.setStatus("UNINSTALLED");
    engineNodeRepository.save(engineNode);
  }
}
