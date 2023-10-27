package com.isxcode.star.modules.cluster.service.biz;

import com.isxcode.star.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.star.api.cluster.constants.ClusterStatus;
import com.isxcode.star.api.cluster.pojos.dto.ScpFileEngineNodeDto;
import com.isxcode.star.api.cluster.pojos.req.*;
import com.isxcode.star.api.cluster.pojos.res.PageClusterRes;
import com.isxcode.star.common.utils.AesUtils;
import com.isxcode.star.modules.cluster.entity.ClusterEntity;
import com.isxcode.star.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.star.modules.cluster.mapper.ClusterMapper;
import com.isxcode.star.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.star.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.star.modules.cluster.repository.ClusterRepository;
import com.isxcode.star.modules.cluster.run.RunAgentCheckService;
import com.isxcode.star.modules.cluster.service.ClusterService;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/** 计算引擎模块. */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ClusterBizService {

	private final ClusterRepository clusterRepository;

	private final ClusterNodeRepository clusterNodeRepository;

	private final ClusterMapper clusterMapper;

	private final RunAgentCheckService runAgentCheckService;

	private final ClusterNodeMapper clusterNodeMapper;

	private final AesUtils aesUtils;

	private final ClusterService clusterService;

	public void addCluster(AddClusterReq addClusterReq) {

		ClusterEntity cluster = clusterMapper.addEngineReqToClusterEntity(addClusterReq);
		clusterRepository.save(cluster);
	}

	public void updateCluster(UpdateClusterReq updateClusterReq) {

		ClusterEntity cluster = clusterService.getCluster(updateClusterReq.getClusterId());
		cluster = clusterMapper.updateEngineReqToClusterEntity(updateClusterReq, cluster);
		clusterRepository.save(cluster);
	}

	public Page<PageClusterRes> pageCluster(PageClusterReq pageClusterReq) {

		Page<ClusterEntity> clusterPage = clusterRepository.pageCluster(pageClusterReq.getSearchKeyWord(),
				PageRequest.of(pageClusterReq.getPage(), pageClusterReq.getPageSize()));

		return clusterPage.map(clusterMapper::clusterEntityToPageClusterRes);
	}

	public void deleteCluster(DeleteClusterReq deleteClusterReq) {

		clusterRepository.deleteById(deleteClusterReq.getEngineId());
	}

	public void checkCluster(CheckClusterReq checkClusterReq) {

		ClusterEntity cluster = clusterService.getCluster(checkClusterReq.getEngineId());

		List<ClusterNodeEntity> engineNodes = clusterNodeRepository.findAllByClusterId(checkClusterReq.getEngineId());

		// 同步检测按钮
		engineNodes.forEach(e -> {
			ScpFileEngineNodeDto scpFileEngineNodeDto = clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(e);
			scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

			try {
				runAgentCheckService.checkAgent(scpFileEngineNodeDto, e);
			} catch (JSchException | IOException | InterruptedException | SftpException ex) {
				log.error(ex.getMessage());
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

	public void setDefaultCluster(SetDefaultClusterReq setDefaultClusterReq) {

		// 检查集群是否存在
		ClusterEntity cluster = clusterService.getCluster(setDefaultClusterReq.getClusterId());

		// 将租户下的所有其他集群的默认集群变为false
		List<ClusterEntity> clusterEntities = clusterRepository.findAll();
		clusterEntities.forEach(e -> e.setDefaultCluster(false));
		clusterRepository.saveAll(clusterEntities);

		// 将指定的集群的默认集群变为true
		cluster.setDefaultCluster(true);
		clusterRepository.save(cluster);
	}
}
