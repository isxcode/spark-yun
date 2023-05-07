package com.isxcode.star.backend.module.cluster.node.service;

import com.isxcode.star.api.constants.CalculateEngineStatus;
import com.isxcode.star.api.constants.EngineNodeStatus;
import com.isxcode.star.api.constants.PathConstants;
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

    // 运行卸载脚本
    String checkCommand = "java -version && yarn application -list | grep 'Total number of applications' && echo $HADOOP_HOME";
    String executeLog;
    try {
      executeLog = executeCommand(scpFileEngineNodeDto, checkCommand, false);
      if (executeLog.contains("SY_ERROR")) {
        engineNode.setStatus(EngineNodeStatus.CAN_NOT_INSTALL);
        engineNodeRepository.save(engineNode);
        throw new SparkYunException("不可安装", executeLog);
      }
    } catch (JSchException | InterruptedException | IOException e) {
      log.error(e.getMessage());
      engineNode.setStatus(EngineNodeStatus.CAN_NOT_INSTALL);
      engineNodeRepository.save(engineNode);
      throw new SparkYunException("不可安装", e.getMessage());
    }

    try {
      engineNode.setAllMemory(getAllMemory(scpFileEngineNodeDto));
      engineNode.setUsedMemory(getUsedMemory(scpFileEngineNodeDto));
      engineNode.setAllStorage(getAllStorage(scpFileEngineNodeDto));
      engineNode.setUsedStorage(getUsedStorage(scpFileEngineNodeDto));
      engineNode.setCpuPercent(getCpuPercent(scpFileEngineNodeDto));
    } catch (Exception e) {
      log.error(e.getMessage());
      engineNode.setStatus(EngineNodeStatus.CHECK_ERROR);
      engineNodeRepository.save(engineNode);
      throw new SparkYunException("检测失败", e.getMessage());
    }

    // 修改状态
    if (!EngineNodeStatus.ACTIVE.equals(engineNode.getStatus())) {
      engineNode.setStatus(EngineNodeStatus.CAN_INSTALL);
    }

    // 保存HadoopHomePath
    String[] split = executeLog.split("\n");
    engineNode.setHadoopHomePath(split[split.length - 1]);

    engineNode.setCheckDateTime(LocalDateTime.now());
    engineNodeRepository.save(engineNode);
    return new EnoCheckAgentRes(executeLog);
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

  public Double getAllMemory(ScpFileEngineNodeDto engineNode) throws JSchException, IOException, InterruptedException {
    String getAllMemoryCommand = "grep MemTotal /proc/meminfo | awk '{print $2/1024/1024}' | bc -l | awk '{printf \"%.2f\\n\", $1}'";
    String allMemory = executeCommand(engineNode, getAllMemoryCommand, false).replace("\n", "");
    return Double.parseDouble(new DecimalFormat("#.00").format(Double.parseDouble(allMemory)));
  }

  public Double getUsedMemory(ScpFileEngineNodeDto engineNode) throws JSchException, IOException, InterruptedException {
    String getUsedMemoryCommand = "free | grep Mem: | awk '{print $3/1024/1024}'";
    String usedMemory = executeCommand(engineNode, getUsedMemoryCommand, false).replace("\n", "");
    return Double.parseDouble(new DecimalFormat("#.00").format(Double.parseDouble(usedMemory)));
  }

  public Double getAllStorage(ScpFileEngineNodeDto engineNode) throws JSchException, IOException, InterruptedException {
    String getAllStorageCommand = "lsblk -b | grep disk | awk '{total += $4} END {print total/1024/1024/1024}'";
    String allStorage = executeCommand(engineNode, getAllStorageCommand, false).replace("\n", "");
    return Double.parseDouble(new DecimalFormat("#.00").format(Double.parseDouble(allStorage)));
  }

  public Double getUsedStorage(ScpFileEngineNodeDto engineNode) throws JSchException, IOException, InterruptedException {
    String getUsedStorageCommand = "df -h -t ext4 | awk '{total += $3} END {print total}'";
    String usedStorage = executeCommand(engineNode, getUsedStorageCommand, false).replace("\n", "");
    return Double.parseDouble(new DecimalFormat("#.00").format(Double.parseDouble(usedStorage)));
  }

  public String getCpuPercent(ScpFileEngineNodeDto engineNode) throws JSchException, IOException, InterruptedException {
    String getCpuPercentCommand = "top -bn1 | grep '%Cpu' | awk '{print 100-$8}'";
    return executeCommand(engineNode, getCpuPercentCommand, false).replace("\n", "");
  }

}
