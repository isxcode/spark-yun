package com.isxcode.star.backend.module.work.run;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.constants.api.PathConstants;
import com.isxcode.star.api.constants.cluster.ClusterNodeStatus;
import com.isxcode.star.api.constants.work.WorkFileType;
import com.isxcode.star.api.constants.work.WorkLog;
import com.isxcode.star.api.exceptions.WorkRunException;
import com.isxcode.star.api.pojos.base.BaseResponse;
import com.isxcode.star.api.pojos.cluster.node.dto.ScpFileEngineNodeDto;
import com.isxcode.star.api.pojos.plugin.req.PluginReq;
import com.isxcode.star.api.pojos.work.res.WokRunWorkRes;
import com.isxcode.star.api.pojos.yun.agent.req.SparkSubmit;
import com.isxcode.star.api.pojos.yun.agent.req.YagExecuteWorkReq;
import com.isxcode.star.api.properties.SparkYunProperties;
import com.isxcode.star.backend.module.cluster.ClusterEntity;
import com.isxcode.star.backend.module.cluster.ClusterRepository;
import com.isxcode.star.backend.module.cluster.node.ClusterNodeEntity;
import com.isxcode.star.backend.module.cluster.node.ClusterNodeRepository;
import com.isxcode.star.backend.module.work.file.WorkFileRepository;
import com.isxcode.star.backend.module.work.instance.WorkInstanceEntity;
import com.isxcode.star.backend.module.work.instance.WorkInstanceRepository;
import com.isxcode.star.backend.module.workflow.instance.WorkflowInstanceRepository;
import com.isxcode.star.common.utils.AesUtils;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.isxcode.star.common.utils.SshUtils.scpFile;

@Service
@Slf4j
public class SparkJarExecutor extends SparkSqlExecutor {

  private final WorkFileRepository workFileRepository;

  private final ClusterRepository clusterRepository;

  private final ClusterNodeRepository clusterNodeRepository;

  private final SparkYunProperties sparkYunProperties;

  public SparkJarExecutor(
      WorkInstanceRepository workInstanceRepository,
      WorkFileRepository workFileRepository,
      ClusterRepository clusterRepository,
      ClusterNodeRepository clusterNodeRepository,
      WorkflowInstanceRepository workflowInstanceRepository,
      SparkYunProperties sparkYunProperties) {

    super(workInstanceRepository, clusterRepository, clusterNodeRepository, workflowInstanceRepository);
    this.workFileRepository = workFileRepository;
    this.clusterRepository = clusterRepository;
    this.clusterNodeRepository = clusterNodeRepository;
    this.sparkYunProperties = sparkYunProperties;
  }

  @Override
  protected void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance) {

    // 获取日志构造器
    StringBuilder logBuilder = workRunContext.getLogBuilder();

    // 检测计算集群是否存在
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始申请资源 \n");
    if (Strings.isEmpty(workRunContext.getClusterId())) {
      throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在  \n");
    }

    // 检查计算集群是否存在
    Optional<ClusterEntity> calculateEngineEntityOptional =
        clusterRepository.findById(workRunContext.getClusterId());
    if (!calculateEngineEntityOptional.isPresent()) {
      throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在  \n");
    }

    // 检测集群中是否有合法节点
    List<ClusterNodeEntity> allEngineNodes =
        clusterNodeRepository.findAllByClusterIdAndStatus(
            calculateEngineEntityOptional.get().getId(), ClusterNodeStatus.RUNNING);
    if (allEngineNodes.isEmpty()) {
      throw new WorkRunException(
          LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 集群不存在可用节点，请切换一个集群  \n");
    }

    // 节点选择随机数
    ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));
    logBuilder
        .append(LocalDateTime.now())
        .append(WorkLog.SUCCESS_INFO)
        .append("申请资源完成，激活节点:【")
        .append(engineNode.getName())
        .append("】\n");
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测运行环境完成  \n");
    workInstance = updateInstance(workInstance, logBuilder);

    // 传输jar与其依赖
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始传输jar与其依赖  \n");
    ScpFileEngineNodeDto scpFileEngineNodeDto = new ScpFileEngineNodeDto();
    scpFileEngineNodeDto.setHost(engineNode.getHost());
    scpFileEngineNodeDto.setPort(engineNode.getPort());
    scpFileEngineNodeDto.setUsername(engineNode.getUsername());
    scpFileEngineNodeDto.setPasswd(
        AesUtils.decrypt(sparkYunProperties.getAesSlat(), engineNode.getPasswd()));

    workFileRepository
        .searchAllByWorkId(WorkFileType.JAR, workInstance.getWorkId())
        .forEach(
            workFileEntity -> {
              try {
                scpFile(
                    scpFileEngineNodeDto,
                    workFileEntity.getFilePath(),
                    engineNode.getAgentHomePath()
                        + File.separator
                        + PathConstants.AGENT_PATH_NAME
                        + File.separator
                        + "plugins"
                        + File.separator
                        + workFileEntity.getFileName());
              } catch (JSchException | SftpException | InterruptedException e) {
                throw new RuntimeException(e);
              }
            });

    workFileRepository
        .searchAllByWorkId(WorkFileType.LIB, workInstance.getWorkId())
        .forEach(
            workFileEntity -> {
              try {
                scpFile(
                    scpFileEngineNodeDto,
                    workFileEntity.getFilePath(),
                    engineNode.getAgentHomePath()
                        + File.separator
                        + PathConstants.AGENT_PATH_NAME
                        + File.separator
                        + "lib"
                        + File.separator
                        + workFileEntity.getFileName());
              } catch (JSchException | SftpException | InterruptedException e) {
                throw new RuntimeException(e);
              }
            });

    // 开始构建作业
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始构建作业  \n");
    YagExecuteWorkReq executeReq = new YagExecuteWorkReq();

    // 开始构造SparkSubmit
    SparkSubmit sparkSubmit =
        SparkSubmit.builder()
            .verbose(true)
            .mainClass(workRunContext.getMainClass())
            .appResource(workRunContext.getJarName())
            .conf(genSparkSubmitConfig(workRunContext.getSparkConfig()))
            .build();

    // 开始构造PluginReq
    PluginReq pluginReq =
        PluginReq.builder()
            .limit(200)
          .sql(workRunContext.getSqlScript())
            .sparkConfig(genSparkConfig(workRunContext.getSparkConfig()))
            .args(workRunContext.getArgs())
            .build();

    // 开始构造executeReq
    executeReq.setSparkSubmit(sparkSubmit);
    executeReq.setPluginReq(pluginReq);
    executeReq.setAgentHomePath(
        engineNode.getAgentHomePath() + File.separator + PathConstants.AGENT_PATH_NAME);
    executeReq.setAgentType(calculateEngineEntityOptional.get().getClusterType());

    // 构建作业完成，并打印作业配置信息
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("构建作业完成 \n");
    workRunContext
        .getSparkConfig()
        .forEach(
            (k, v) ->
                logBuilder
                    .append(LocalDateTime.now())
                    .append(WorkLog.SUCCESS_INFO)
                    .append(k)
                    .append(":")
                    .append(v)
                    .append(" \n"));
    logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始提交作业  \n");
    workInstance = updateInstance(workInstance, logBuilder);

    BaseResponse<?> baseResponse = submitWork(engineNode, executeReq);

    // 解析返回对象,获取appId
    WokRunWorkRes submitWorkRes =
        JSON.parseObject(JSON.toJSONString(baseResponse.getData()), WokRunWorkRes.class);
    logBuilder
        .append(LocalDateTime.now())
        .append(WorkLog.SUCCESS_INFO)
        .append("提交作业成功 : ")
        .append(submitWorkRes.getAppId())
        .append("\n");
    workInstance = updateInstance(workInstance, logBuilder);

    checkWorkStatus(workInstance, logBuilder, calculateEngineEntityOptional, engineNode, submitWorkRes);
  }

}
