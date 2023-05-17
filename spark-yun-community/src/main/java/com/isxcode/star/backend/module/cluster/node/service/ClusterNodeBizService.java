package com.isxcode.star.backend.module.cluster.node.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.constants.CalculateEngineStatus;
import com.isxcode.star.api.constants.EngineNodeStatus;
import com.isxcode.star.api.constants.PathConstants;
import com.isxcode.star.api.pojos.engine.node.dto.AgentCheckInfo;
import com.isxcode.star.api.pojos.engine.node.dto.ScpFileEngineNodeDto;
import com.isxcode.star.api.pojos.engine.node.req.EnoAddNodeReq;
import com.isxcode.star.api.pojos.engine.node.req.EnoQueryNodeReq;
import com.isxcode.star.api.pojos.engine.node.req.EnoUpdateNodeReq;
import com.isxcode.star.api.pojos.engine.node.res.EnoCheckAgentRes;
import com.isxcode.star.api.pojos.engine.node.res.EnoInstallAgentRes;
import com.isxcode.star.api.pojos.engine.node.res.EnoQueryNodeRes;
import com.isxcode.star.api.pojos.engine.node.res.EnoRemoveAgentRes;
import com.isxcode.star.api.properties.SparkYunProperties;
import com.isxcode.star.backend.module.cluster.entity.ClusterEntity;
import com.isxcode.star.backend.module.cluster.node.entity.ClusterNodeEntity;
import com.isxcode.star.backend.module.cluster.node.repository.ClusterNodeRepository;
import com.isxcode.star.backend.module.cluster.repository.ClusterRepository;
import com.isxcode.star.backend.module.cluster.node.mapper.ClusterNodeMapper;
import com.isxcode.star.api.exception.SparkYunException;
import com.jcraft.jsch.JSchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.isxcode.star.api.utils.SshUtils.executeCommand;
import static com.isxcode.star.api.utils.SshUtils.scpFile;

/**
 * 用户模块接口的业务逻辑.
 */
@Service
@RequiredArgsConstructor
@Transactional(noRollbackFor = {SparkYunException.class})
@Slf4j
public class ClusterNodeBizService {

  private final ClusterNodeRepository engineNodeRepository;

  private final ClusterRepository calculateEngineRepository;

  private final ClusterNodeMapper engineNodeMapper;

  private final SparkYunProperties sparkYunProperties;

  public void addNode(EnoAddNodeReq enoAddNodeReq) {

    // 检查计算引擎是否存在
    Optional<ClusterEntity> calculateEngineEntityOptional = calculateEngineRepository.findById(enoAddNodeReq.getClusterId());
    if (!calculateEngineEntityOptional.isPresent()) {
      throw new SparkYunException("计算引擎不存在");
    }

    ClusterNodeEntity node = engineNodeMapper.addNodeReqToNodeEntity(enoAddNodeReq);

    // 设置代理端口号
    if (Strings.isEmpty(enoAddNodeReq.getPort())) {
      node.setPort("22");
    }

    // 设置安装地址
    node.setAgentHomePath(
      getDefaultAgentHomePath(enoAddNodeReq.getAgentHomePath(), enoAddNodeReq.getUsername()));

    // 设置代理端口号
    if (Strings.isEmpty(enoAddNodeReq.getAgentPort())) {
      node.setAgentPort(sparkYunProperties.getDefaultAgentPort());
    }

    // 初始化节点状态，未检测
    node.setStatus(EngineNodeStatus.NEW);

    if (Strings.isEmpty(enoAddNodeReq.getHadoopHomePath())) {
      node.setHadoopHomePath("/opt/hadoop");
    }

    engineNodeRepository.save(node);
  }

  public void updateNode(EnoUpdateNodeReq enoUpdateNodeReq) {

    // 检查计算引擎是否存在
    Optional<ClusterEntity> calculateEngineEntityOptional = calculateEngineRepository.findById(enoUpdateNodeReq.getClusterId());
    if (!calculateEngineEntityOptional.isPresent()) {
      throw new SparkYunException("计算引擎不存在");
    }

    // 判断节点存不存在
    Optional<ClusterNodeEntity> engineNodeEntityOptional = engineNodeRepository.findById(enoUpdateNodeReq.getId());
    if (!engineNodeEntityOptional.isPresent()) {
      throw new SparkYunException("计算引擎不存在");
    }

    ClusterNodeEntity node = engineNodeMapper.updateNodeReqToNodeEntity(enoUpdateNodeReq);
    node.setId(engineNodeEntityOptional.get().getId());

    // 设置安装地址
    node.setAgentHomePath(
      getDefaultAgentHomePath(enoUpdateNodeReq.getAgentHomePath(), enoUpdateNodeReq.getUsername()));

    // 设置代理端口号
    if (Strings.isEmpty(enoUpdateNodeReq.getAgentPort())) {
      node.setAgentPort(sparkYunProperties.getDefaultAgentPort());
    }

    // 初始化节点状态，未检测
    node.setStatus(EngineNodeStatus.UN_CHECK);
    engineNodeRepository.save(node);

    // 集群状态修改
    ClusterEntity calculateEngineEntity = calculateEngineEntityOptional.get();
    calculateEngineEntity.setStatus(CalculateEngineStatus.UN_CHECK);
    calculateEngineRepository.save(calculateEngineEntity);
  }

  public String getDefaultAgentHomePath(String agentHomePath, String username) {

    if (Strings.isEmpty(agentHomePath)) {

      if ("root".equals(username)) {
        return "/root";
      } else {
        return "/home/" + username ;
      }
    } else {
      return agentHomePath;
    }
  }

  public Page<EnoQueryNodeRes> queryNodes(EnoQueryNodeReq enoQueryNodeReq) {

    Page<ClusterNodeEntity> engineNodeEntities =
      engineNodeRepository.searchAll(enoQueryNodeReq.getSearchKeyWord(),
        enoQueryNodeReq.getClusterId(),
        PageRequest.of(enoQueryNodeReq.getPage(), enoQueryNodeReq.getPageSize()));

    return engineNodeMapper.datasourceEntityPageToQueryDatasourceResPage(engineNodeEntities);
  }

  public void delNode(String nodeId) {

    Optional<ClusterNodeEntity> engineNodeEntityOptional = engineNodeRepository.findById(nodeId);
    if (!engineNodeEntityOptional.isPresent()) {
      throw new SparkYunException("节点已删除");
    }

    // 判断节点状态是否为已安装
    if (EngineNodeStatus.ACTIVE.equals(engineNodeEntityOptional.get().getStatus())) {
      throw new SparkYunException("请卸载节点后删除");
    }

    engineNodeRepository.deleteById(nodeId);
  }

  public ClusterNodeEntity getEngineNode(String engineNodeId) {

    Optional<ClusterNodeEntity> engineNodeEntityOptional =
      engineNodeRepository.findById(engineNodeId);

    if (!engineNodeEntityOptional.isPresent()) {
      throw new SparkYunException("节点不存在");
    }

    return engineNodeEntityOptional.get();
  }

  /**
   * 安装节点.
   */
  public EnoInstallAgentRes installAgent(String engineNodeId) {

    ClusterNodeEntity engineNode = getEngineNode(engineNodeId);

    if (Strings.isEmpty(engineNode.getHadoopHomePath())) {
      throw new SparkYunException("请填写hadoop配置");
    }

    ScpFileEngineNodeDto scpFileEngineNodeDto = engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);

    // 拷贝安装包
    scpFile(
      scpFileEngineNodeDto,
      sparkYunProperties.getAgentTarGzDir()
        + File.separator
        + PathConstants.SPARK_YUN_AGENT_TAR_GZ_NAME,
      engineNode.getAgentHomePath() + File.separator + PathConstants.SPARK_YUN_AGENT_TAR_GZ_NAME);

    // 拷贝执行脚本
    scpFile(
      scpFileEngineNodeDto,
      sparkYunProperties.getAgentBinDir()
        + File.separator
        + PathConstants.AGENT_INSTALL_BASH_NAME,
      engineNode.getAgentHomePath() + File.separator + PathConstants.AGENT_INSTALL_BASH_NAME);

    // 运行安装脚本
    String installCommand =
      "bash " + engineNode.getAgentHomePath() + File.separator + PathConstants.AGENT_INSTALL_BASH_NAME
        + " --home-path=" + engineNode.getAgentHomePath()
        + " --agent-port=" + engineNode.getAgentPort()
        + " --hadoop-home=" + engineNode.getHadoopHomePath();

    String executeLog;
    try {
      executeLog = executeCommand(scpFileEngineNodeDto, installCommand, false);
    } catch (JSchException | InterruptedException | IOException e) {
      log.error(e.getMessage());
      engineNode.setStatus(EngineNodeStatus.INSTALL_ERROR);
      engineNodeRepository.save(engineNode);
      throw new SparkYunException("安装失败", e.getMessage());
    }

    engineNode.setStatus(EngineNodeStatus.ACTIVE);

    engineNodeRepository.save(engineNode);

    return new EnoInstallAgentRes(executeLog);
  }

  public EnoCheckAgentRes checkAgent(String engineNodeId) {

    ClusterNodeEntity engineNode = getEngineNode(engineNodeId);

    ScpFileEngineNodeDto scpFileEngineNodeDto = engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);

    // 拷贝执行脚本
    scpFile(
      scpFileEngineNodeDto,
      sparkYunProperties.getAgentBinDir()
        + File.separator
        + PathConstants.AGENT_CHECK_BASH_NAME,
      engineNode.getAgentHomePath() + File.separator + PathConstants.AGENT_CHECK_BASH_NAME);

    // 运行安装脚本
    String checkCommand =
      "bash " + engineNode.getAgentHomePath() + File.separator + PathConstants.AGENT_CHECK_BASH_NAME
        + " --home-path=" + engineNode.getAgentHomePath();

    // 获取返回结果
    AgentCheckInfo agentCheckInfo;
    try {
       String executeLog = executeCommand(scpFileEngineNodeDto, checkCommand, false);
       agentCheckInfo = JSON.parseObject(executeLog, AgentCheckInfo.class);
    } catch (JSchException | InterruptedException | IOException e) {
      log.error(e.getMessage());
      engineNode.setStatus(EngineNodeStatus.CAN_NOT_INSTALL);
      engineNodeRepository.save(engineNode);
      throw new SparkYunException("不可安装", e.getMessage());
    }

    engineNode.setAllMemory(agentCheckInfo.getAllMemory());
    engineNode.setUsedMemory(agentCheckInfo.getUsedMemory());
    engineNode.setAllStorage(agentCheckInfo.getAllStorage());
    engineNode.setUsedStorage(agentCheckInfo.getUsedStorage());
    engineNode.setCpuPercent(agentCheckInfo.getCpuPercent());

    // 修改状态  未安装 -> 停止 -> 运行中
    if (EngineNodeStatus.RUNNING.equals(agentCheckInfo.getRunStatus())) {
      engineNode.setStatus(EngineNodeStatus.RUNNING);
    }
    if (EngineNodeStatus.STOP.equals(agentCheckInfo.getRunStatus())) {
      engineNode.setStatus(EngineNodeStatus.STOP);
    }
    if (EngineNodeStatus.NO_INSTALL.equals(agentCheckInfo.getInstallStatus())) {
      engineNode.setStatus(EngineNodeStatus.NO_INSTALL);
    }

    engineNode.setCheckDateTime(LocalDateTime.now());
    engineNodeRepository.save(engineNode);
    return new EnoCheckAgentRes("检测成功");
  }

  public EnoRemoveAgentRes removeAgent(String engineNodeId) {

    ClusterNodeEntity engineNode = getEngineNode(engineNodeId);

    ScpFileEngineNodeDto scpFileEngineNodeDto = engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);

    // 运行卸载脚本
    String installCommand =
      "bash " + engineNode.getAgentHomePath() + File.separator + "spark-yun-agent" + File.separator + "bin" + File.separator + PathConstants.AGENT_REMOVE_BASH_NAME + " --home-path=" + engineNode.getAgentHomePath();
    String executeLog;
    try {
      executeLog = executeCommand(scpFileEngineNodeDto, installCommand, false);
    } catch (JSchException | InterruptedException | IOException e) {
      log.error(e.getMessage());
      engineNode.setStatus(EngineNodeStatus.ACTIVE);
      engineNodeRepository.save(engineNode);
      throw new SparkYunException("卸载失败", e.getMessage());
    }

    engineNode.setStatus(EngineNodeStatus.UNINSTALLED);
    engineNodeRepository.save(engineNode);

    return new EnoRemoveAgentRes(executeLog);
  }

}
