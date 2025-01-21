package com.isxcode.star.modules.cluster.run;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.cluster.dto.AgentInfo;
import com.isxcode.star.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.star.api.main.properties.SparkYunProperties;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.common.utils.os.OsUtils;
import com.isxcode.star.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.star.modules.cluster.repository.ClusterNodeRepository;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

import static com.isxcode.star.common.utils.ssh.SshUtils.executeCommand;
import static com.isxcode.star.common.utils.ssh.SshUtils.scpFile;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(noRollbackFor = {IsxAppException.class})
public class RunAgentCleanService {

    private final SparkYunProperties sparkYunProperties;

    private final ClusterNodeRepository clusterNodeRepository;

    public void run(String clusterNodeId, ScpFileEngineNodeDto scpFileEngineNodeDto, String tenantId, String userId) {

        // 获取节点信息
        Optional<ClusterNodeEntity> clusterNodeEntityOptional = clusterNodeRepository.findById(clusterNodeId);
        if (!clusterNodeEntityOptional.isPresent()) {
            return;
        }
        ClusterNodeEntity clusterNodeEntity = clusterNodeEntityOptional.get();

        try {
            cleanAgent(scpFileEngineNodeDto, clusterNodeEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException("清理失败");
        }
    }

    public void cleanAgent(ScpFileEngineNodeDto scpFileEngineNodeDto, ClusterNodeEntity engineNode)
        throws JSchException, IOException, InterruptedException, SftpException {

        String bashFilePath = sparkYunProperties.getTmpDir() + "/agent-clean.sh";

        // 拷贝检测脚本
        scpFile(scpFileEngineNodeDto, "classpath:bash/agent-clean.sh", bashFilePath);

        // 运行清理脚本
        String cleanCommand = "bash " + bashFilePath + " --user=" + engineNode.getUsername();
        log.debug("执行远程命令:{}", cleanCommand);

        // 获取返回结果
        String executeLog =
            executeCommand(scpFileEngineNodeDto, OsUtils.fixWindowsChar(bashFilePath, cleanCommand), false);
        log.debug("远程返回值:{}", executeLog);

        AgentInfo agentStartInfo = JSON.parseObject(executeLog, AgentInfo.class);

        // 修改状态
        if (!"CLEAN_SUCCESS".equals(agentStartInfo.getStatus())) {
            throw new IsxAppException("清理失败");
        }
    }
}
