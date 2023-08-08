package com.isxcode.star.common.utils.ssh;

import com.isxcode.star.api.cluster.pojos.dto.ScpFileEngineNodeDto;
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

/** ssh连接工具类. */
@Slf4j
public class SshUtils {

  /** scp传递文件. */
  public static void scpFile(ScpFileEngineNodeDto engineNode, String srcPath, String dstPath)
      throws JSchException, SftpException, InterruptedException {

    // 初始化jsch
    JSch jsch = new JSch();
    Session session =
        jsch.getSession(
            engineNode.getUsername(), engineNode.getHost(), Integer.parseInt(engineNode.getPort()));

    // 连接远程服务器
    session.setPassword(engineNode.getPasswd());
    session.setConfig("StrictHostKeyChecking", "no");
    session.connect();

    // 上传文件
    ChannelSftp channel;
    channel = (ChannelSftp) session.openChannel("sftp");
    channel.connect(120000);
    channel.put(srcPath, dstPath);

    // 文件校验
    File file = new File(srcPath);
    SftpATTRS attrs;
    while (true) {
      attrs = channel.stat(dstPath);
      if (attrs != null) {
        long remoteFileSize = attrs.getSize();
        long localFileSize = file.length();
        if (remoteFileSize == localFileSize) {
          break;
        }
      }
      Thread.sleep(1000);
    }

    channel.disconnect();
    session.disconnect();
  }

  /** 执行远程命令. */
  public static String executeCommand(ScpFileEngineNodeDto engineNode, String command, boolean pty)
      throws JSchException, InterruptedException, IOException {

    JSch jsch = new JSch();
    Session session;

    session =
        jsch.getSession(
            engineNode.getUsername(), engineNode.getHost(), Integer.parseInt(engineNode.getPort()));

    session.setPassword(engineNode.getPasswd());
    session.setConfig("StrictHostKeyChecking", "no");
    session.connect();

    ChannelExec channel = (ChannelExec) session.openChannel("exec");
    channel.setPty(pty);
    channel.setCommand(command);
    channel.setInputStream(null);
    channel.setErrStream(null);

    InputStream in = channel.getInputStream();
    InputStream err = channel.getErrStream();
    channel.connect();
    BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
    String line;
    StringBuilder output = new StringBuilder();
    while ((line = reader.readLine()) != null) {
      output.append(line).append("\n");
    }

    BufferedReader errReader =
        new BufferedReader(new InputStreamReader(err, StandardCharsets.UTF_8));
    String errLine;
    StringBuilder errOutput = new StringBuilder();
    while ((errLine = errReader.readLine()) != null) {
      errOutput.append(errLine).append("\n");
    }

    while (!channel.isClosed()) {
      Thread.sleep(1000);
    }

    // 判断命令是否执行完成
    int exitStatus = channel.getExitStatus();
    channel.disconnect();
    session.disconnect();

    if (exitStatus != 0) {
      return "{\n"
          + "        \"execStatus\":\"ERROR\",\n"
          + "        \"log\":\""
          + errOutput
          + "\"\n"
          + "      }";
    } else {
      return output.toString();
    }
  }
}
