package com.isxcode.star.modules.cluster.service;

import com.isxcode.star.api.cluster.pojos.dto.ScpFileEngineNodeDto;
import com.isxcode.star.api.main.properties.SparkYunProperties;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.star.modules.cluster.repository.ClusterNodeRepository;
import com.jcraft.jsch.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.isxcode.star.common.utils.ssh.SshUtils.scpFile;

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

	public void checkScpPercent(ScpFileEngineNodeDto engineNode, String srcPath, String dstPath,
			ClusterNodeEntity clusterNode) throws JSchException, IOException, InterruptedException {

		// 初始化jsch
		JSch jsch = new JSch();

		if (engineNode.getPasswd().length() > 1000) {
			jsch.addIdentity(engineNode.getUsername(), engineNode.getPasswd().getBytes(), null, null);
		}

		Session session = jsch.getSession(engineNode.getUsername(), engineNode.getHost(),
				Integer.parseInt(engineNode.getPort()));

		// 连接远程服务器
		if (engineNode.getPasswd().length() < 1000) {
			session.setPassword(engineNode.getPasswd());
		}

		session.setConfig("StrictHostKeyChecking", "no");
		session.connect();

		// 初始化sftp功能
		ChannelSftp channel;
		channel = (ChannelSftp) session.openChannel("sftp");
		channel.connect(120000);
		FileSystemResourceLoader resourceLoader = new FileSystemResourceLoader();

		// 文件校验
		SftpATTRS attrs;

		int scpPercent = 0;
		while (scpPercent < 100) {

			try {
				attrs = channel.stat(dstPath);
			} catch (Exception e) {
				log.error(e.getMessage());
				continue;
			}

			if (attrs != null) {
				long remoteFileSize = attrs.getSize();
				long localFileSize = resourceLoader.getResource(srcPath).contentLength();
				scpPercent = (int) (remoteFileSize * 100 / localFileSize);
			}

			clusterNode = clusterNodeRepository.findById(clusterNode.getId()).get();
			clusterNode.setAgentLog(clusterNode.getAgentLog() + "\n进度:" + scpPercent + "%");
			clusterNodeRepository.saveAndFlush(clusterNode);

			Thread.sleep(10000);
		}

		channel.disconnect();
		session.disconnect();
	}

	@Async("sparkYunThreadPool")
	public void scpAgentFile(ScpFileEngineNodeDto clusterNode, String srcPath, String dstPath)
			throws JSchException, SftpException, IOException, InterruptedException {

		scpFile(clusterNode, srcPath, dstPath);
	}
}
