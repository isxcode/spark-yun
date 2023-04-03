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
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

/** 用户模块接口的业务逻辑. */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
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

    if (Strings.isEmpty(node.getHomePath())) {
      if ("root".equals(node.getUsername())) {
        node.setHomePath("/root/");
      } else {
        node.setHomePath("/home/" + node.getUsername() + "/");
      }
    } else {
      node.setHomePath(addNodeReq.getHomePath());
    }

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
      throws JSchException, SftpException, IOException, InterruptedException {

    // 查询节点信息
    NodeEntity node = nodeRepository.findById(nodeId).get();

    // 拷贝安装包
    scpFile(node, "/spark-yun-agent.tar.gz", node.getHomePath() + "spark-yun-agent.tar.gz");

    scpFile(node, "/spark-yun-install", node.getHomePath() + "spark-yun-install");

    // 运行安装脚本
    String installCommand = "bash " + node.getHomePath() + "spark-yun-install";
    executeCommand(node, installCommand, false);

    node.setStatus("已安装");
    nodeRepository.save(node);

    // 安装成功
    return new InstallAgentRes("安装成功");
  }

  public CheckAgentRes checkAgent(String nodeId)
      throws JSchException, IOException, InterruptedException {

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
      throws JSchException, SftpException, IOException, InterruptedException {

    // 查询节点信息
    NodeEntity node = nodeRepository.findById(nodeId).get();

    // 拷贝卸载脚本
    scpFile(node, "/spark-yun-remove", node.getHomePath());

    // 运行卸载脚本s
    String installCommand = "bash ~/spark-yun-remove";
    executeCommand(node, installCommand, false);

    node.setStatus("已卸载");
    nodeRepository.save(node);

    return new RemoveAgentRes("卸载成功");
  }

  public void scpFile(NodeEntity node, String srcPath, String dstPath)
      throws JSchException, SftpException, InterruptedException {

    JSch jsch = new JSch();
    Session session =
        jsch.getSession(node.getUsername(), node.getHost(), Integer.parseInt(node.getPort()));
    session.setPassword(node.getPasswd());
    session.setConfig("StrictHostKeyChecking", "no");
    session.connect();

    ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
    channel.connect(1000);
    channel.put(srcPath, dstPath);

    File file = new File(srcPath);
    SftpATTRS attrs;
    while (true) {
      attrs = channel.stat(dstPath);
      if (attrs != null) {
        long remoteFileSize = attrs.getSize();
        long localFileSize = file.length();
        log.info("remoteFileSize: {} localFileSize: {}", remoteFileSize, localFileSize);
        if (remoteFileSize == localFileSize) {
          break;
        }
      }
      Thread.sleep(1000);
    }

    channel.disconnect();
    session.disconnect();
  }

  public void executeCommand(NodeEntity node, String command, boolean pty)
      throws JSchException, IOException, InterruptedException {

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

    while (!channel.isClosed()) {
      Thread.sleep(1000);
    }

    int exitStatus = channel.getExitStatus();
    if (exitStatus >= 0) {
      System.out.println("命令执行完成，退出状态：" + exitStatus);
    } else {
      System.out.println("命令执行失败，退出状态：" + exitStatus);
    }

    channel.disconnect();
    session.disconnect();
  }
}
