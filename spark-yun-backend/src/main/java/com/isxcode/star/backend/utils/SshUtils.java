package com.isxcode.star.backend.utils;

import com.isxcode.star.backend.module.engine.node.entity.EngineNodeEntity;
import com.isxcode.star.common.exception.SparkYunException;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SshUtils {

  /** scp传递文件. */
  public static void scpFile(EngineNodeEntity engineNode, String srcPath, String dstPath) {

    // 初始化jsch
    JSch jsch = new JSch();
    Session session;
    try {
      session =
          jsch.getSession(
              engineNode.getUsername(),
              engineNode.getHost(),
              Integer.parseInt(engineNode.getPort()));
    } catch (JSchException e) {
      log.error(e.getMessage());
      throw new SparkYunException("无法连接远程服务器", e.getMessage());
    }

    // 连接远程服务器
    session.setPassword(engineNode.getPasswd());
    session.setConfig("StrictHostKeyChecking", "no");
    try {
      session.connect();
    } catch (JSchException e) {
      log.error(e.getMessage());
      throw new SparkYunException("无法连接远程服务器", e.getMessage());
    }

    // 上传文件
    ChannelSftp channel;
    try {
      channel = (ChannelSftp) session.openChannel("sftp");
      channel.connect(1000);
      channel.put(srcPath, dstPath);
    } catch (JSchException | SftpException e) {
      log.error(e.getMessage());
      throw new SparkYunException("分发代理文件失败", e.getMessage());
    }

    // 文件校验
    File file = new File(srcPath);
    SftpATTRS attrs;
    while (true) {
      try {
        attrs = channel.stat(dstPath);
      } catch (SftpException e) {
        log.error(e.getMessage());
        throw new SparkYunException("分发代理文件失败", e.getMessage());
      }
      if (attrs != null) {
        long remoteFileSize = attrs.getSize();
        long localFileSize = file.length();
        if (remoteFileSize == localFileSize) {
          break;
        }
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        log.error(e.getMessage());
        throw new SparkYunException("等待分发线程异常", e.getMessage());
      }
    }

    channel.disconnect();
    session.disconnect();
  }

  /**
   * 执行远程命令.
   */
  public static String executeCommand(EngineNodeEntity engineNode, String command, boolean pty) throws JSchException, InterruptedException, IOException {

    JSch jsch = new JSch();
    Session session;

    session =
      jsch.getSession(
        engineNode.getUsername(),
        engineNode.getHost(),
        Integer.parseInt(engineNode.getPort()));

    session.setPassword(engineNode.getPasswd());
    session.setConfig("StrictHostKeyChecking", "no");
    session.connect();

    ChannelExec channel = (ChannelExec) session.openChannel("exec");
    channel.setPty(pty);
    channel.setCommand(command);
    channel.setInputStream(null);
//      channel.setErrStream(System.out);

    InputStream in = channel.getInputStream();
    channel.connect();
    BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
    String line;
    StringBuilder output = new StringBuilder();
    while ((line = reader.readLine()) != null) {
      output.append(line).append("\n");
    }

    while (!channel.isClosed()) {
      Thread.sleep(1000);
    }

    // 判断命令是否执行完成
    int exitStatus = channel.getExitStatus();
    channel.disconnect();
    session.disconnect();

    if (exitStatus != 0) {
      throw new SparkYunException("执行安装脚本异常", output.toString());
    } else {
      return output.toString();
    }
  }
}
