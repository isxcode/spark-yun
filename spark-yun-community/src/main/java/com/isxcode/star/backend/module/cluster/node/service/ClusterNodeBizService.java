package com.isxcode.star.backend.module.cluster.node.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.constants.CalculateEngineStatus;
import com.isxcode.star.api.constants.EngineNodeStatus;
import com.isxcode.star.api.constants.PathConstants;
import com.isxcode.star.api.pojos.engine.node.dto.AgentEnvInfo;
import com.isxcode.star.api.pojos.engine.node.dto.ScpFileEngineNodeDto;
import com.isxcode.star.api.pojos.engine.node.req.EnoAddNodeReq;
import com.isxcode.star.api.pojos.engine.node.req.EnoQueryNodeReq;
import com.isxcode.star.api.pojos.engine.node.req.EnoUpdateNodeReq;
import com.isxcode.star.api.pojos.engine.node.res.EnoQueryNodeRes;
import com.isxcode.star.api.pojos.engine.node.res.EnoRemoveAgentRes;
import com.isxcode.star.api.properties.SparkYunProperties;
import com.isxcode.star.api.utils.AesUtils;
import com.isxcode.star.backend.module.cluster.entity.ClusterEntity;
import com.isxcode.star.backend.module.cluster.node.entity.ClusterNodeEntity;
import com.isxcode.star.backend.module.cluster.node.repository.ClusterNodeRepository;
import com.isxcode.star.backend.module.cluster.repository.ClusterRepository;
import com.isxcode.star.backend.module.cluster.node.mapper.ClusterNodeMapper;
import com.isxcode.star.api.exception.SparkYunException;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static com.isxcode.star.api.utils.SshUtils.executeCommand;
import static com.isxcode.star.api.utils.SshUtils.scpFile;
import static com.isxcode.star.backend.config.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.backend.config.WebSecurityConfig.USER_ID;

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

  private final RunAgentCheckService runAgentCheckService;

  private final RunAgentInstallService runAgentInstallService;

  public void addNode(EnoAddNodeReq enoAddNodeReq) {

    // 检查计算引擎是否存在
    Optional<ClusterEntity> calculateEngineEntityOptional = calculateEngineRepository.findById(enoAddNodeReq.getClusterId());
    if (!calculateEngineEntityOptional.isPresent()) {
      throw new SparkYunException("计算引擎不存在");
    }
    ClusterNodeEntity node = engineNodeMapper.addNodeReqToNodeEntity(enoAddNodeReq);

    // 密码对成加密
    node.setPasswd(AesUtils.encrypt(sparkYunProperties.getAesSlat(), enoAddNodeReq.getPasswd()));

    // 设置服务器默认端口号
    node.setPort(enoAddNodeReq.getPort() == null ? "22" : enoAddNodeReq.getPort());

    // 设置默认代理安装地址
    node.setAgentHomePath(getDefaultAgentHomePath(enoAddNodeReq.getAgentHomePath(), enoAddNodeReq.getUsername()));

    // 设置默认代理端口号
    node.setAgentPort(getDefaultAgentPort(enoAddNodeReq.getAgentPort()));

    // 初始化节点状态，未检测
    node.setStatus(EngineNodeStatus.UN_INSTALL);

    // 持久化数据
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

    // 转换对象
    ClusterNodeEntity node = engineNodeMapper.updateNodeReqToNodeEntity(enoUpdateNodeReq);
    node.setId(engineNodeEntityOptional.get().getId());

    // 设置安装地址
    node.setAgentHomePath(getDefaultAgentHomePath(enoUpdateNodeReq.getAgentHomePath(), enoUpdateNodeReq.getUsername()));

    // 设置代理端口号
    node.setAgentPort(getDefaultAgentPort(enoUpdateNodeReq.getAgentPort()));

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
        return "/home/" + username;
      }
    } else {
      return agentHomePath;
    }
  }

  public String getDefaultAgentPort(String agentPort) {

    if (Strings.isEmpty(agentPort)) {
      return sparkYunProperties.getDefaultAgentPort();
    } else {
      return agentPort;
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
    if (EngineNodeStatus.RUNNING.equals(engineNodeEntityOptional.get().getStatus())) {
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

  public void checkAgent(String engineNodeId) {

    // 获取节点信息
    ClusterNodeEntity engineNode = getEngineNode(engineNodeId);

    // 如果是安装中等状态，需要等待运行结束
    if (EngineNodeStatus.CHECKING.equals(engineNode.getStatus())
      || EngineNodeStatus.INSTALLING.equals(engineNode.getStatus())
      || EngineNodeStatus.REMOVING.equals(engineNode.getStatus())) {
      throw new SparkYunException("进行中，稍后再试");
    }

    // 转换请求节点检测对象
    ScpFileEngineNodeDto scpFileEngineNodeDto = engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);

    // 修改状态
    engineNode.setStatus(EngineNodeStatus.CHECKING);

    // 持久化
    engineNodeRepository.saveAndFlush(engineNode);

    // 异步调用
    runAgentCheckService.run(engineNodeId, scpFileEngineNodeDto, TENANT_ID.get(), USER_ID.get());
  }

  /**
   * 安装节点.
   */
  public void installAgent(String engineNodeId) {

    // 获取节点信息
    ClusterNodeEntity engineNode = getEngineNode(engineNodeId);

    // 如果是安装中等状态，需要等待运行结束
    if (EngineNodeStatus.CHECKING.equals(engineNode.getStatus())
      || EngineNodeStatus.INSTALLING.equals(engineNode.getStatus())
      || EngineNodeStatus.REMOVING.equals(engineNode.getStatus())) {
      throw new SparkYunException("进行中，稍后再试");
    }

    // 将节点信息转成工具类识别对象
    ScpFileEngineNodeDto scpFileEngineNodeDto = engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
    scpFileEngineNodeDto.setPasswd(AesUtils.decrypt(sparkYunProperties.getAesSlat(), scpFileEngineNodeDto.getPasswd()));

    // 修改状态
    engineNode.setStatus(EngineNodeStatus.INSTALLING);

    // 持久化
    engineNodeRepository.saveAndFlush(engineNode);

    // 异步调用
    runAgentInstallService.run(engineNodeId, scpFileEngineNodeDto, TENANT_ID.get(), USER_ID.get());
  }

  public EnoRemoveAgentRes removeAgent(String engineNodeId) {

    ClusterNodeEntity engineNode = getEngineNode(engineNodeId);

    ScpFileEngineNodeDto scpFileEngineNodeDto = engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);

    // 运行卸载脚本
    String installCommand =
      "bash " + engineNode.getAgentHomePath() + File.separator + "spark-yun-agent" + File.separator + "bin" + File.separator + PathConstants.AGENT_UNINSTALL_BASH_NAME + " --home-path=" + engineNode.getAgentHomePath();
    String executeLog;
    try {
      executeLog = executeCommand(scpFileEngineNodeDto, installCommand, false);
    } catch (JSchException | InterruptedException | IOException e) {
      log.error(e.getMessage());
      engineNode.setStatus(EngineNodeStatus.RUNNING);
      engineNodeRepository.save(engineNode);
      throw new SparkYunException("卸载失败", e.getMessage());
    }

    engineNode.setStatus(EngineNodeStatus.UN_INSTALL);
    engineNodeRepository.save(engineNode);

    return new EnoRemoveAgentRes(executeLog);
  }

}
