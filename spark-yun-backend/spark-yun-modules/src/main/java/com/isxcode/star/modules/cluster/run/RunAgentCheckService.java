package com.isxcode.star.modules.cluster.run;

import static com.isxcode.star.common.config.CommonConfig.TENANT_ID;
import static com.isxcode.star.common.config.CommonConfig.USER_ID;
import static com.isxcode.star.common.utils.ssh.SshUtils.executeCommand;
import static com.isxcode.star.common.utils.ssh.SshUtils.scpFile;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.star.api.cluster.constants.ClusterStatus;
import com.isxcode.star.api.cluster.dto.AgentInfo;
import com.isxcode.star.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.star.api.main.properties.SparkYunProperties;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.common.utils.os.OsUtils;
import com.isxcode.star.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.star.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.star.modules.cluster.repository.ClusterRepository;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(noRollbackFor = {IsxAppException.class})
public class RunAgentCheckService {

    private final SparkYunProperties sparkYunProperties;

    private final ClusterNodeRepository clusterNodeRepository;

    private final ClusterRepository clusterRepository;

    @Async("sparkYunWorkThreadPool")
    public void run(String clusterNodeId, ScpFileEngineNodeDto scpFileEngineNodeDto, String tenantId, String userId) {

        USER_ID.set(userId);
        TENANT_ID.set(tenantId);

        // 获取节点信息
        Optional<ClusterNodeEntity> clusterNodeEntityOptional = clusterNodeRepository.findById(clusterNodeId);
        if (!clusterNodeEntityOptional.isPresent()) {
            return;
        }
        ClusterNodeEntity clusterNodeEntity = clusterNodeEntityOptional.get();

        try {
            checkAgent(scpFileEngineNodeDto, clusterNodeEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            clusterNodeEntity.setCheckDateTime(LocalDateTime.now());
            clusterNodeEntity.setAgentLog(e.getMessage());
            clusterNodeEntity.setStatus(ClusterNodeStatus.CHECK_ERROR);
            clusterNodeRepository.saveAndFlush(clusterNodeEntity);
        }
    }

    public void checkAgent(ScpFileEngineNodeDto scpFileEngineNodeDto, ClusterNodeEntity engineNode)
        throws JSchException, IOException, InterruptedException, SftpException {

        String bashFilePath = sparkYunProperties.getTmpDir() + "/agent-check.sh";

        // 拷贝检测脚本
        scpFile(scpFileEngineNodeDto, "classpath:bash/agent-check.sh", bashFilePath);

        // 运行安装脚本
        String checkCommand = "bash " + bashFilePath + " --home-path=" + engineNode.getAgentHomePath();

        log.debug("执行远程命令:{}", checkCommand);

        // 获取返回结果
        String executeLog =
            executeCommand(scpFileEngineNodeDto, OsUtils.fixWindowsChar(bashFilePath, checkCommand), false);

        log.debug("远程返回值:{}", executeLog);
        AgentInfo agentCheckInfo = JSON.parseObject(executeLog, AgentInfo.class);

        if (agentCheckInfo == null) {
            return;
        }

        // 保存服务器信息
        engineNode.setAllMemory(
            Double.parseDouble(Strings.isEmpty(agentCheckInfo.getAllMemory()) ? "0.0" : agentCheckInfo.getAllMemory()));
        engineNode.setUsedMemory(Double
            .parseDouble(Strings.isEmpty(agentCheckInfo.getUsedMemory()) ? "0.0" : agentCheckInfo.getUsedMemory()));
        engineNode.setAllStorage(Double
            .parseDouble(Strings.isEmpty(agentCheckInfo.getAllStorage()) ? "0.0" : agentCheckInfo.getAllStorage()));
        engineNode.setUsedStorage(Double
            .parseDouble(Strings.isEmpty(agentCheckInfo.getUsedStorage()) ? "0.0" : agentCheckInfo.getUsedStorage()));
        engineNode.setCpuPercent(Double
            .parseDouble(Strings.isEmpty(agentCheckInfo.getCpuPercent()) ? "0.0" : agentCheckInfo.getCpuPercent()));

        // 修改状态
        engineNode.setStatus(agentCheckInfo.getStatus());
        engineNode.setAgentLog(agentCheckInfo.getLog());
        engineNode.setCheckDateTime(LocalDateTime.now());
        clusterNodeRepository.saveAndFlush(engineNode);

        // 如果状态是成功的话,将集群改为启用
        if (ClusterNodeStatus.RUNNING.equals(agentCheckInfo.getStatus())) {
            clusterRepository.findById(engineNode.getClusterId()).ifPresent(clusterEntity -> {
                clusterEntity.setStatus(ClusterStatus.ACTIVE);
                clusterRepository.saveAndFlush(clusterEntity);
            });
        }
    }
}
