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
    node.setPasswd(aesUtils.encrypt(enoAddNodeReq.getPasswd().trim()));

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
