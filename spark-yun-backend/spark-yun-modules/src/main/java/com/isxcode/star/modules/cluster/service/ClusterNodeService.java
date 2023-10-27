package com.isxcode.star.modules.cluster.service;

import com.isxcode.star.api.main.properties.SparkYunProperties;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.star.modules.cluster.repository.ClusterNodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClusterNodeService {

	private final SparkYunProperties sparkYunProperties;

	private final ClusterNodeRepository clusterNodeRepository;

	/**
	 * 获取代理安装路径
	 *
	 * @param agentHomePath
	 *            代理的安装目录
	 * @param username
	 *            节点的用户名
	 * @return 代理安装的路径
	 */
	public String getDefaultAgentHomePath(String agentHomePath, String username) {

		if ("root".equals(username)) {
			return "/root";
		} else {
			return "/home/" + username;
		}
	}

	/**
	 * 获取代理默认端口号
	 *
	 * @param agentPort
	 *            代理端口号
	 * @return 代理端口号
	 */
	public String getDefaultAgentPort(String agentPort) {

		if (Strings.isEmpty(agentPort)) {
			return sparkYunProperties.getDefaultAgentPort();
		} else {
			return agentPort;
		}
	}

	/**
	 * 获取集群节点
	 *
	 * @param clusterNodeId
	 *            集群节点id
	 * @return 集群节点entity对象
	 */
	public ClusterNodeEntity getClusterNode(String clusterNodeId) {

		return clusterNodeRepository.findById(clusterNodeId).orElseThrow(() -> new IsxAppException("节点不存在"));
	}
}
