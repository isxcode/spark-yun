package com.isxcode.star.modules.cluster.service;

import static com.isxcode.star.security.main.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.security.main.WebSecurityConfig.USER_ID;

import com.isxcode.star.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.star.api.cluster.constants.ClusterStatus;
import com.isxcode.star.api.cluster.pojos.dto.ScpFileEngineNodeDto;
import com.isxcode.star.api.cluster.pojos.req.EnoAddNodeReq;
import com.isxcode.star.api.cluster.pojos.req.EnoQueryNodeReq;
import com.isxcode.star.api.cluster.pojos.req.EnoUpdateNodeReq;
import com.isxcode.star.api.cluster.pojos.res.EnoQueryNodeRes;
import com.isxcode.star.backend.api.base.exceptions.SparkYunException;
import com.isxcode.star.backend.api.base.properties.SparkYunProperties;
import com.isxcode.star.common.utils.AesUtils;
import com.isxcode.star.modules.cluster.entity.ClusterEntity;
import com.isxcode.star.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.star.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.star.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.star.modules.cluster.repository.ClusterRepository;
import com.isxcode.star.modules.cluster.run.RunAgentCheckService;
import com.isxcode.star.modules.cluster.run.RunAgentInstallService;
import com.isxcode.star.modules.cluster.run.RunAgentRemoveService;
import com.isxcode.star.modules.cluster.run.RunAgentStartService;
import com.isxcode.star.modules.cluster.run.RunAgentStopService;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 用户模块接口的业务逻辑. */
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

  private final RunAgentStopService runAgentStopService;

  private final RunAgentStartService runAgentStartService;

  private final RunAgentRemoveService runAgentRemoveService;

  private final AesUtils aesUtils;

  public void addNode(EnoAddNodeReq enoAddNodeReq) {

    // 检查计算引擎是否存在
    Optional<ClusterEntity> calculateEngineEntityOptional =
        calculateEngineRepository.findById(enoAddNodeReq.getClusterId());
    if (!calculateEngineEntityOptional.isPresent()) {
      throw new SparkYunException("计算引擎不存在");
    }
    ClusterNodeEntity node = engineNodeMapper.addNodeReqToNodeEntity(enoAddNodeReq);

    // 密码对成加密
//    node.setPasswd(aesUtils.encrypt(enoAddNodeReq.getPasswd().trim()));
    node.setPasswd(aesUtils.encrypt("-----BEGIN OPENSSH PRIVATE KEY-----\n" +
      "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\n" +
      "-----END OPENSSH PRIVATE KEY-----"));

    // 设置服务器默认端口号
    node.setPort(Strings.isEmpty(enoAddNodeReq.getPort()) ? "22" : enoAddNodeReq.getPort().trim());

    // 设置默认代理安装地址
    node.setAgentHomePath(
        getDefaultAgentHomePath(
            enoAddNodeReq.getAgentHomePath(), enoAddNodeReq.getUsername().trim()));

    // 添加特殊逻辑，从备注中获取安装路径
    // 正则获取路径 $path{/root}
    if (!Strings.isEmpty(enoAddNodeReq.getRemark())) {

      Pattern regex = Pattern.compile("\\$path\\{(.*?)\\}");
      Matcher matcher = regex.matcher(enoAddNodeReq.getRemark());
      if (matcher.find()) {
        node.setAgentHomePath(matcher.group(1));
      }
    }

    // 设置默认代理端口号
    node.setAgentPort(getDefaultAgentPort(enoAddNodeReq.getAgentPort().trim()));

    // 初始化节点状态，未检测
    node.setStatus(ClusterNodeStatus.UN_INSTALL);

    // 持久化数据
    engineNodeRepository.save(node);
  }

  public void updateNode(EnoUpdateNodeReq enoUpdateNodeReq) {

    // 检查计算引擎是否存在
    Optional<ClusterEntity> calculateEngineEntityOptional =
        calculateEngineRepository.findById(enoUpdateNodeReq.getClusterId());
    if (!calculateEngineEntityOptional.isPresent()) {
      throw new SparkYunException("计算引擎不存在");
    }

    // 判断节点存不存在
    Optional<ClusterNodeEntity> engineNodeEntityOptional =
        engineNodeRepository.findById(enoUpdateNodeReq.getId());
    if (!engineNodeEntityOptional.isPresent()) {
      throw new SparkYunException("计算引擎不存在");
    }
    ClusterNodeEntity clusterNodeEntity = engineNodeEntityOptional.get();

    // 转换对象
    ClusterNodeEntity node =
        engineNodeMapper.updateNodeReqToNodeEntity(enoUpdateNodeReq, clusterNodeEntity);

    // 设置安装地址
    node.setAgentHomePath(
        getDefaultAgentHomePath(
            enoUpdateNodeReq.getAgentHomePath(), enoUpdateNodeReq.getUsername()));

    // 添加特殊逻辑，从备注中获取安装路径
    // 正则获取路径 $path{/root}
    if (!Strings.isEmpty(enoUpdateNodeReq.getRemark())) {

      Pattern regex = Pattern.compile("\\$path\\{(.*?)\\}");
      Matcher matcher = regex.matcher(enoUpdateNodeReq.getRemark());
      if (matcher.find()) {
        node.setAgentHomePath(matcher.group(1));
      }
    }

    // 密码对成加密
    node.setPasswd(aesUtils.encrypt(enoUpdateNodeReq.getPasswd()));

    // 设置代理端口号
    node.setAgentPort(getDefaultAgentPort(enoUpdateNodeReq.getAgentPort()));

    // 初始化节点状态，未检测
    node.setStatus(ClusterNodeStatus.UN_CHECK);
    engineNodeRepository.save(node);

    // 集群状态修改
    ClusterEntity calculateEngineEntity = calculateEngineEntityOptional.get();
    calculateEngineEntity.setStatus(ClusterStatus.UN_CHECK);
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
        engineNodeRepository.searchAll(
            enoQueryNodeReq.getSearchKeyWord(),
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
    if (ClusterNodeStatus.RUNNING.equals(engineNodeEntityOptional.get().getStatus())) {
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
    if (ClusterNodeStatus.CHECKING.equals(engineNode.getStatus())
        || ClusterNodeStatus.INSTALLING.equals(engineNode.getStatus())
        || ClusterNodeStatus.REMOVING.equals(engineNode.getStatus())) {
      throw new SparkYunException("进行中，稍后再试");
    }

    // 转换请求节点检测对象
    ScpFileEngineNodeDto scpFileEngineNodeDto =
        engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
    scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

    // 修改状态
    engineNode.setStatus(ClusterNodeStatus.CHECKING);
    engineNode.setAgentLog("检测中");

    // 持久化
    engineNodeRepository.saveAndFlush(engineNode);

    // 异步调用
    runAgentCheckService.run(engineNodeId, scpFileEngineNodeDto, TENANT_ID.get(), USER_ID.get());
  }

  /** 安装节点. */
  public void installAgent(String engineNodeId) {

    // 获取节点信息
    ClusterNodeEntity engineNode = getEngineNode(engineNodeId);

    // 查询集群信息
    Optional<ClusterEntity> clusterEntityOptional =
        calculateEngineRepository.findById(engineNode.getClusterId());
    if (!clusterEntityOptional.isPresent()) {
      throw new SparkYunException("集群不存在");
    }

    // 如果是安装中等状态，需要等待运行结束
    if (ClusterNodeStatus.CHECKING.equals(engineNode.getStatus())
        || ClusterNodeStatus.INSTALLING.equals(engineNode.getStatus())
        || ClusterNodeStatus.REMOVING.equals(engineNode.getStatus())) {
      throw new SparkYunException("进行中，稍后再试");
    }

    // 将节点信息转成工具类识别对象
    ScpFileEngineNodeDto scpFileEngineNodeDto =
        engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
    scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

    // 修改状态
    engineNode.setStatus(ClusterNodeStatus.INSTALLING);
    engineNode.setAgentLog("激活中");

    // 持久化
    engineNodeRepository.saveAndFlush(engineNode);

    // 异步调用
    runAgentInstallService.run(
        engineNodeId,
        clusterEntityOptional.get().getClusterType(),
        scpFileEngineNodeDto,
        TENANT_ID.get(),
        USER_ID.get());
  }

  public void removeAgent(String engineNodeId) {

    // 获取节点信息
    ClusterNodeEntity engineNode = getEngineNode(engineNodeId);

    // 如果是安装中等状态，需要等待运行结束
    if (ClusterNodeStatus.CHECKING.equals(engineNode.getStatus())
        || ClusterNodeStatus.INSTALLING.equals(engineNode.getStatus())
        || ClusterNodeStatus.REMOVING.equals(engineNode.getStatus())) {
      throw new SparkYunException("进行中，稍后再试");
    }

    // 将节点信息转成工具类识别对象
    ScpFileEngineNodeDto scpFileEngineNodeDto =
        engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
    scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

    // 修改状态
    engineNode.setStatus(ClusterNodeStatus.REMOVING);
    engineNode.setAgentLog("卸载中");

    // 持久化
    engineNodeRepository.saveAndFlush(engineNode);

    // 异步调用
    runAgentRemoveService.run(engineNodeId, scpFileEngineNodeDto, TENANT_ID.get(), USER_ID.get());
  }

  /** 停止节点. */
  public void stopAgent(String engineNodeId) {

    // 获取节点信息
    ClusterNodeEntity engineNode = getEngineNode(engineNodeId);

    // 如果是安装中等状态，需要等待运行结束
    if (ClusterNodeStatus.CHECKING.equals(engineNode.getStatus())
        || ClusterNodeStatus.INSTALLING.equals(engineNode.getStatus())
        || ClusterNodeStatus.REMOVING.equals(engineNode.getStatus())) {
      throw new SparkYunException("进行中，稍后再试");
    }

    // 将节点信息转成工具类识别对象
    ScpFileEngineNodeDto scpFileEngineNodeDto =
        engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
    scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

    // 修改状态
    engineNode.setStatus(ClusterNodeStatus.STOPPING);
    engineNode.setAgentLog("停止中");

    // 持久化
    engineNodeRepository.saveAndFlush(engineNode);

    // 异步调用
    runAgentStopService.run(engineNodeId, scpFileEngineNodeDto, TENANT_ID.get(), USER_ID.get());
  }

  /** 激活中. */
  public void startAgent(String engineNodeId) {

    // 获取节点信息
    ClusterNodeEntity engineNode = getEngineNode(engineNodeId);

    // 如果是安装中等状态，需要等待运行结束
    if (ClusterNodeStatus.CHECKING.equals(engineNode.getStatus())
        || ClusterNodeStatus.INSTALLING.equals(engineNode.getStatus())
        || ClusterNodeStatus.REMOVING.equals(engineNode.getStatus())) {
      throw new SparkYunException("进行中，稍后再试");
    }

    // 将节点信息转成工具类识别对象
    ScpFileEngineNodeDto scpFileEngineNodeDto =
        engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
    scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

    // 修改状态
    engineNode.setStatus(ClusterNodeStatus.STARTING);
    engineNode.setAgentLog("启动中");

    // 持久化
    engineNodeRepository.saveAndFlush(engineNode);

    // 异步调用
    runAgentStartService.run(engineNodeId, scpFileEngineNodeDto, TENANT_ID.get(), USER_ID.get());
  }
}
