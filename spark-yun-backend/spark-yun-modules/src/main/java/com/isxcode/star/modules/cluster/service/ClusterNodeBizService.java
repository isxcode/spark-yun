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

    // 密码对成加密
    //    node.setPasswd(aesUtils.encrypt(enoUpdateNodeReq.getPasswd()));
    node.setPasswd(
        aesUtils.encrypt(
            "-----BEGIN OPENSSH PRIVATE KEY-----\n"
                + "b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAABlwAAAAdzc2gtcn\n"
                + "NhAAAAAwEAAQAAAYEAqqNohrENJikxlZDHhXjSWTWwPlmI2HAt4etKOuyH3TsgHEsAd2/e\n"
                + "KIOgKRlqXDDHRWWCv/b4ONuUhbLbkI5l6hrC2mtIOFjZzaqpeKvcKB62qb40thIAsDKEra\n"
                + "JWZkmYt3qauRa/j4R/X2dt44YILndhFCtaMznJXR28YHmIxCBQwSuQXDkxNHuYFC8o4y+2\n"
                + "6ZSkdFkvYtN89YlVf9370NCftCK3oFPxShOxKF4GKlUF8hv4QPd6hElrZSxq+qNSUQsd50\n"
                + "lS+f+7QA8LSUrNLel42GWrpm90juQ9haSd2pAKEog8yylrzH3Jwlq1RHgDBG9VF618SPku\n"
                + "+MFSWRMfvfb2TKfFWV3pmwwxkHE9EwF0nMVooX2L4J7SaU8E19pONBIxaNFWDtmkehiCE3\n"
                + "VxTr7SS+uqBSPrFextFoBptFmO+72X8jTFWwc0ysSYUi8q+wdWe1kYYXTiFpbsm7QOkj2l\n"
                + "ggcCx5BEERqFioq/zI1mwiMHKhvbop8lCV5pMGenAAAFkKxSk/ysUpP8AAAAB3NzaC1yc2\n"
                + "EAAAGBAKqjaIaxDSYpMZWQx4V40lk1sD5ZiNhwLeHrSjrsh907IBxLAHdv3iiDoCkZalww\n"
                + "x0Vlgr/2+DjblIWy25COZeoawtprSDhY2c2qqXir3Cgetqm+NLYSALAyhK2iVmZJmLd6mr\n"
                + "kWv4+Ef19nbeOGCC53YRQrWjM5yV0dvGB5iMQgUMErkFw5MTR7mBQvKOMvtumUpHRZL2LT\n"
                + "fPWJVX/d+9DQn7Qit6BT8UoTsSheBipVBfIb+ED3eoRJa2UsavqjUlELHedJUvn/u0APC0\n"
                + "lKzS3peNhlq6ZvdI7kPYWkndqQChKIPMspa8x9ycJatUR4AwRvVRetfEj5LvjBUlkTH732\n"
                + "9kynxVld6ZsMMZBxPRMBdJzFaKF9i+Ce0mlPBNfaTjQSMWjRVg7ZpHoYghN1cU6+0kvrqg\n"
                + "Uj6xXsbRaAabRZjvu9l/I0xVsHNMrEmFIvKvsHVntZGGF04haW7Ju0DpI9pYIHAseQRBEa\n"
                + "hYqKv8yNZsIjByob26KfJQleaTBnpwAAAAMBAAEAAAGBAIlA9XPZ+DSoz9x5XzOqErB1Mf\n"
                + "WE+UD3QsDFDKlG1qYJx/NaFN79WZoG/g3xBo9vIOpeE4Srd8/sN5QvYNLu52Ud73vW7Xl9\n"
                + "Znw6+SQ6QNT4l38DosyMwIb5q6Uqvgp6yl9TPB8DAETFk9q+jDKF/hC6L5kUofIwWhsPLx\n"
                + "X+zBbN04b9rpmv2zdW7uXVZTMQjzVlPb7keewpJwstU1uNx96X4M35WvCYdK5cFpTcxGLK\n"
                + "jwTGoLJJHgBIOh1gVBCUyvG9k5wWWY7hpup5SAloj7ydR5M6qiY/lTIzhYAUGLycLP13tU\n"
                + "x0YMoqBvPSZvnxgH09u1zZzFkNlQbD5BvlRGVTRTZsGyOU5+p6NnnyMOvYceInRiTOMq/o\n"
                + "drNU+SdbjL3HnhlGu+OzMwnVRnK60iyL/j8GK+U3KwZwsk/oMHmJu0CUd6E92i4Em2Y5fg\n"
                + "CW6dWU0Tk9m5rW45hGjR+jvz9qW2SuT1L7eEpKBlLj3qGWEgLz/K1uLcR38xXdGveQQQAA\n"
                + "AMB3tsq+JYP65ZFpHRDiQg8bT6iiNZ9ULaw8cRz95uqz56EbFNTNNUeBljTcM6SQKLhVzp\n"
                + "RSInzZFV5QaTCjz5kUeiGedDSEf0laIUVr53aQ3t9TgNmZUDEdqlyRi/Wy58IKC4TUmmPL\n"
                + "bBy1LfUCPgUUxAeucSI1PKVOWZF7mpu4jW2pdZevo5vFmfV9NjA1lIkxUAess6BSMpLWNB\n"
                + "C5+U854pPihObmhqpNbgsymJppqIt/TxBdkLdA/3BHoE9oOjAAAADBAN7ftmw8b4vaBenl\n"
                + "oLAFj8VoGBdCxhpBjBEvQn1ygQnDpeVm6XfNuk+4bs2YEKmvmI0YViN5wNVzn5tRNYZg1U\n"
                + "lkB8O77f2dgu93nDQ19qSww/lKWqAimGr1Z+OYd8L8Xf2ClIOmSz/kvOAwnkJkmyR9h4K1\n"
                + "YoveKKp1aUq/rWfn3adstWeziPg6QqEAu9Z16LseuMlJTv1bE0jEl+iNdj8BQwO4cJceAh\n"
                + "cacxJFhWB3IbcCmtoqf8VKLPocZEesIQAAAMEAxAAlvhZCB6irU74M//V9LOxe/7QpUpM+\n"
                + "EyCGXbtVST/suQH6rEsQa614r+atf+ubfySQrJXc0dOcMZOCw0GlwX6bBIR43HK/EVG/Ef\n"
                + "6vBVvnjj0LCuVwz1thz6RlWHuRjq1GmCcgkoAHgcc3IGMmwO0GzXHXlmU7Yyg49vJ/CGbF\n"
                + "mlhfUMBXa+5z0UnjpprwV0EtLfnVA8xFHgcQQk40HVK4RSPUqTCH/Hc7rklAZeqEMJpNs3\n"
                + "L7eTm3sy50NlrHAAAAF2lzcG9uZ0Bpc3BvbmctbWFjLmxvY2FsAQID\n"
                + "-----END OPENSSH PRIVATE KEY-----"));

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
