package com.isxcode.star.backend.module.node.service;

import com.isxcode.star.api.pojos.node.req.AddNodeReq;
import com.isxcode.star.api.pojos.node.res.CheckAgentRes;
import com.isxcode.star.api.pojos.node.res.InstallAgentRes;
import com.isxcode.star.api.pojos.node.res.QueryNodeRes;
import com.isxcode.star.api.pojos.node.res.RemoveAgentRes;
import com.isxcode.star.backend.module.node.entity.NodeEntity;
import com.isxcode.star.backend.module.node.mapper.NodeMapper;
import com.isxcode.star.backend.module.node.repository.NodeRepository;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 用户模块接口的业务逻辑. */
@Service
@RequiredArgsConstructor
@Transactional
public class NodeBizService {

  private final NodeRepository nodeRepository;

  private final NodeMapper nodeMapper;

  public void addNode(AddNodeReq addNodeReq) {

    // req 转 entity
    NodeEntity node = nodeMapper.addNodeReqToNodeEntity(addNodeReq);
    node.setActiveMemory(0);
    node.setAllMemory(0);
    node.setActiveStorage(0);
    node.setAllStorage(0);
    node.setCpuPercent("0%");
    node.setCheckDate(LocalDateTime.now());
    node.setStatus("未安装");

    // 数据持久化
    nodeRepository.save(node);
  }

  public List<QueryNodeRes> queryNodes(String engineId) {

    List<NodeEntity> nodeEntities = nodeRepository.findAllByEngineId(engineId);

    return nodeMapper.nodeEntityListToQueryNodeResList(nodeEntities);
  }

  public void delNode(String nodeId) {

    nodeRepository.deleteById(nodeId);
  }

  public InstallAgentRes installAgent(String nodeId)
      throws JSchException, SftpException, IOException {

    // 查询节点信息
    NodeEntity node = nodeRepository.findById(nodeId).get();

    // 拷贝安装脚本
    scpFile(node, "/spark-yun-install", "~/");

    // 拷贝安装包
    scpFile(node, "/spark-yun-agent.tar.gz", "~/");

    // 运行安装脚本
    String installCommand = "bash ~/spark-yun-install";
    executeCommand(node, installCommand, false);

    node.setStatus("已安装");
    nodeRepository.save(node);

    // 安装成功
    return new InstallAgentRes("安装成功");
  }

  public CheckAgentRes checkAgent(String nodeId) throws JSchException, IOException {

    // 查询节点信息
    NodeEntity node = nodeRepository.findById(nodeId).get();

    // 运行卸载脚本
    String lsCommand = "ls";
    executeCommand(node, lsCommand, false);

    node.setStatus("可安装");
    nodeRepository.save(node);

    return new CheckAgentRes("完成检测");
  }

  public RemoveAgentRes removeAgent(String nodeId)
      throws JSchException, SftpException, IOException {

    // 查询节点信息
    NodeEntity node = nodeRepository.findById(nodeId).get();

    // 拷贝卸载脚本
    scpFile(node, "/spark-yun-remove", "~/");

    // 运行卸载脚本s
    String installCommand = "bash ~/spark-yun-remove";
    executeCommand(node, installCommand, false);

    node.setStatus("已卸载");
    nodeRepository.save(node);

    return new RemoveAgentRes("卸载成功");
  }

  public void scpFile(NodeEntity node, String srcPath, String dstPath)
      throws JSchException, SftpException {

    JSch jsch = new JSch();
    Session session =
        jsch.getSession(node.getUsername(), node.getHost(), Integer.parseInt(node.getPort()));
    session.setPassword(node.getPasswd());
    session.setConfig("StrictHostKeyChecking", "no");
    session.connect();

    ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
    channel.connect(1000);
    channel.put(srcPath, dstPath);

    channel.disconnect();
    session.disconnect();
  }

  public void executeCommand(NodeEntity node, String command, boolean pty)
      throws JSchException, IOException {

    JSch jsch = new JSch();
    Session session =
        jsch.getSession(node.getUsername(), node.getHost(), Integer.parseInt(node.getPort()));
    session.setPassword(node.getPasswd());
    session.setConfig("StrictHostKeyChecking", "no");
    session.connect();

    ChannelExec channel = (ChannelExec) session.openChannel("exec");
    channel.setPty(pty);
    channel.setCommand(command);
    channel.connect();

    channel.disconnect();
    session.disconnect();
  }
}
