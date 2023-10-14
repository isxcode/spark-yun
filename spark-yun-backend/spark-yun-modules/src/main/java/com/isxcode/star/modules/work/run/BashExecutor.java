package com.isxcode.star.modules.work.run;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.cluster.pojos.dto.ScpFileEngineNodeDto;
import com.isxcode.star.api.main.properties.SparkYunProperties;
import com.isxcode.star.api.work.constants.WorkLog;
import com.isxcode.star.api.work.exceptions.WorkRunException;
import com.isxcode.star.common.utils.AesUtils;
import com.isxcode.star.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.star.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.star.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import com.isxcode.star.modules.workflow.repository.WorkflowInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.isxcode.star.common.utils.ssh.SshUtils.executeCommand;
import static com.isxcode.star.common.utils.ssh.SshUtils.scpFile;

@Service
@Slf4j
public class BashExecutor extends WorkExecutor {

  private final WorkInstanceRepository workInstanceRepository;

  private final SparkYunProperties sparkYunProperties;

  private final ClusterNodeRepository clusterNodeRepository;

  private final ClusterNodeMapper clusterNodeMapper;

  private final AesUtils aesUtils;

	public BashExecutor(WorkInstanceRepository workInstanceRepository, WorkflowInstanceRepository workflowInstanceRepository,
                      SparkYunProperties sparkYunProperties, ClusterNodeRepository clusterNodeRepository, ClusterNodeMapper clusterNodeMapper, AesUtils aesUtils) {

		super(workInstanceRepository, workflowInstanceRepository);
    this.workInstanceRepository = workInstanceRepository;
    this.sparkYunProperties = sparkYunProperties;
    this.clusterNodeRepository = clusterNodeRepository;
    this.clusterNodeMapper = clusterNodeMapper;
    this.aesUtils = aesUtils;
  }

	public void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance) {

		// 获取日志构造器
		StringBuilder logBuilder = workRunContext.getLogBuilder();

		// 检查脚本是否为空
		if (Strings.isEmpty(workRunContext.getBashScript())) {
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "bash脚本内容为空 \n");
		}

		// 脚本检查通过
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行作业 \n");
		workInstance = updateInstance(workInstance, logBuilder);

		try {
      //存储本地临时bash脚本文件

      // 创建一个临时文件
      File file = new File(workInstance.getId() + "-bash.sh");
      if (!file.exists()) {
        file.createNewFile();
      }
      FileWriter writer = new FileWriter(file);
      // 写入脚本
      writer.write(workRunContext.getBashScript());
      writer.close();

      Optional<ClusterNodeEntity> clusterNodeEntityOptional = clusterNodeRepository.findById(workRunContext.getClusterNodeId());
      if (!clusterNodeEntityOptional.isPresent()) {
        throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "集群节点不存在 \n");
      }
      logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("成功获取集群节点信息 \n");
      ClusterNodeEntity clusterNode = clusterNodeEntityOptional.get();
      ScpFileEngineNodeDto scpFileEngineNodeDto = clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(clusterNode);
      scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

      scpFile(scpFileEngineNodeDto, "/"+ file.getAbsolutePath(),
        sparkYunProperties.getTmpDir() + File.separator + file.getName());

      // 待运行脚本
      String chCommand = "chmod 755 " + sparkYunProperties.getTmpDir() + File.separator + file.getName();
      String Command = "bash " + sparkYunProperties.getTmpDir() + File.separator + file.getName();
      String delCommand = "rm -f " + sparkYunProperties.getTmpDir() + File.separator + file.getName();

      //授予执行权限
      executeCommand(scpFileEngineNodeDto, chCommand, false);
      // 执行
      String result = executeCommand(scpFileEngineNodeDto, Command, false);
      // 删除远程临时文件
      executeCommand(scpFileEngineNodeDto, delCommand, false);
      // 删除本地临时文件
      file.delete();

			//存储脚本执行结果
			logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("结果保存成功  \n");
			workInstance.setSubmitLog(logBuilder.toString());
			workInstance.setResultData(JSON.toJSONString(result));
			workInstanceRepository.saveAndFlush(workInstance);
		} catch (Exception e) {

			log.error(e.getMessage());
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + e.getMessage() + "\n");
		}
	}

	@Override
	protected void abort(WorkInstanceEntity workInstance) {

		Thread thread = WORK_THREAD.get(workInstance.getId());
		thread.interrupt();
	}
}
