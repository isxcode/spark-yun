package com.isxcode.star.modules.cluster.service;

import com.isxcode.star.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.star.api.cluster.constants.ClusterStatus;
import com.isxcode.star.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.common.utils.aes.AesUtils;
import com.isxcode.star.modules.cluster.entity.ClusterEntity;
import com.isxcode.star.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.star.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.star.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.star.modules.cluster.repository.ClusterRepository;
import com.isxcode.star.modules.cluster.run.RunAgentCheckService;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ClusterService {

    private final ClusterRepository clusterRepository;

    private final ClusterNodeRepository clusterNodeRepository;

    private final ClusterNodeMapper clusterNodeMapper;

    private final AesUtils aesUtils;

    private final RunAgentCheckService runAgentCheckService;

    public ClusterEntity getCluster(String clusterId) {

        return clusterRepository.findById(clusterId).orElseThrow(() -> new IsxAppException("计算引擎不存在"));
    }

    public String getClusterName(String clusterId) {

        ClusterEntity clusterEntity = clusterRepository.findById(clusterId).orElse(null);
        return clusterEntity == null ? clusterId : clusterEntity.getName();
    }

    public void checkCluster(String clusterId) {

        ClusterEntity cluster = getCluster(clusterId);

        List<ClusterNodeEntity> engineNodes = clusterNodeRepository.findAllByClusterId(clusterId);

        // 同步检测按钮
        engineNodes.forEach(e -> {
            ScpFileEngineNodeDto scpFileEngineNodeDto = clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(e);
            scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

            try {
                runAgentCheckService.checkAgent(scpFileEngineNodeDto, e);
            } catch (JSchException | IOException | InterruptedException | SftpException ex) {
                log.error(ex.getMessage(), ex);
                e.setCheckDateTime(LocalDateTime.now());
                e.setAgentLog(ex.getMessage());
                e.setStatus(ClusterNodeStatus.CHECK_ERROR);
                clusterNodeRepository.saveAndFlush(e);
            }
        });

        // 激活节点
        List<ClusterNodeEntity> activeNodes = engineNodes.stream()
            .filter(e -> ClusterNodeStatus.RUNNING.equals(e.getStatus())).collect(Collectors.toList());
        cluster.setActiveNodeNum(activeNodes.size());
        cluster.setAllNodeNum(engineNodes.size());

        // 内存
        double allMemory = activeNodes.stream().mapToDouble(ClusterNodeEntity::getAllMemory).sum();
        cluster.setAllMemoryNum(allMemory);
        double usedMemory = activeNodes.stream().mapToDouble(ClusterNodeEntity::getUsedMemory).sum();
        cluster.setUsedMemoryNum(usedMemory);

        // 存储
        double allStorage = activeNodes.stream().mapToDouble(ClusterNodeEntity::getAllStorage).sum();
        cluster.setAllStorageNum(allStorage);
        double usedStorage = activeNodes.stream().mapToDouble(ClusterNodeEntity::getUsedStorage).sum();
        cluster.setUsedStorageNum(usedStorage);

        if (!activeNodes.isEmpty()) {
            cluster.setStatus(ClusterStatus.ACTIVE);
        } else {
            cluster.setStatus(ClusterStatus.NO_ACTIVE);
        }

        cluster.setCheckDateTime(LocalDateTime.now());
        clusterRepository.saveAndFlush(cluster);
    }
}
