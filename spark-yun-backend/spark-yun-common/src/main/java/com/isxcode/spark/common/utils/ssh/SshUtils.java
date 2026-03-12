package com.isxcode.spark.common.utils.ssh;

import com.isxcode.spark.api.cluster.dto.ScpFileEngineNodeDto;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResourceLoader;

/**
 * ssh连接工具类.
 */
@Slf4j
public class SshUtils {

    /**
     * scp传递文件.
     */
    public static void scpFile(ScpFileEngineNodeDto engineNode, String srcPath, String dstPath)
        throws JSchException, SftpException, InterruptedException, IOException {

        // 初始化jsch
        JSch jsch = new JSch();

        if (engineNode.getPasswd().length() > 1000) {
            jsch.addIdentity(engineNode.getUsername(), engineNode.getPasswd().getBytes(), null, null);
        }

        Session session =
            jsch.getSession(engineNode.getUsername(), engineNode.getHost(), Integer.parseInt(engineNode.getPort()));

        // 连接远程服务器
        if (engineNode.getPasswd().length() < 1000) {
            session.setPassword(engineNode.getPasswd());
        }

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("ConnectTimeout", "30000");
        config.put("ServerAliveInterval", "30000");
        config.put("ServerAliveCountMax", "3");
        session.setConfig(config);
        session.setTimeout(30000);
        session.connect();

        // 上传文件
        ChannelSftp channel;
        channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect(120000);
        FileSystemResourceLoader resourceLoader = new FileSystemResourceLoader();
        channel.put(resourceLoader.getResource(srcPath).getInputStream(), dstPath);

        // 文件校验
        SftpATTRS attrs;
        while (true) {
            attrs = channel.stat(dstPath);
            if (attrs != null) {
                long remoteFileSize = attrs.getSize();
                long localFileSize = resourceLoader.getResource(srcPath).contentLength();
                if (remoteFileSize == localFileSize) {
                    break;
                }
            }
            Thread.sleep(2000);
        }

        channel.disconnect();
        session.disconnect();
    }

    /**
     * 执行远程命令.
     */
    public static String executeCommand(ScpFileEngineNodeDto engineNode, String command, boolean pty)
        throws JSchException, InterruptedException, IOException {

        JSch jsch = new JSch();

        if (engineNode.getPasswd().length() > 1000) {
            jsch.addIdentity(engineNode.getUsername(), engineNode.getPasswd().getBytes(), null, null);
        }

        Session session;

        session =
            jsch.getSession(engineNode.getUsername(), engineNode.getHost(), Integer.parseInt(engineNode.getPort()));

        if (engineNode.getPasswd().length() < 1000) {
            session.setPassword(engineNode.getPasswd());
        }

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("ConnectTimeout", "30000");
        config.put("ServerAliveInterval", "30000");
        config.put("ServerAliveCountMax", "3");
        session.setConfig(config);
        session.setTimeout(30000);
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

        BufferedReader errReader = new BufferedReader(new InputStreamReader(err, StandardCharsets.UTF_8));
        String errLine;
        StringBuilder errOutput = new StringBuilder();
        while ((errLine = errReader.readLine()) != null) {
            errOutput.append(errLine).append("\n");
        }

        while (!channel.isClosed()) {
            Thread.sleep(2000);
        }

        // 判断命令是否执行完成
        int exitStatus = channel.getExitStatus();
        channel.disconnect();
        session.disconnect();

        if (exitStatus != 0) {
            return "{\n" + "        \"execStatus\":\"ERROR\",\n" + "        \"log\":\"" + errOutput + "\"\n"
                + "      }";
        } else {
            return output.toString();
        }
    }

    /**
     * 后台执行远程命令，并尽快返回pid.
     */
    public static String executeBackgroundCommand(ScpFileEngineNodeDto engineNode, String command, boolean pty)
        throws JSchException, InterruptedException, IOException {

        JSch jsch = new JSch();

        if (engineNode.getPasswd().length() > 1000) {
            jsch.addIdentity(engineNode.getUsername(), engineNode.getPasswd().getBytes(), null, null);
        }

        Session session =
            jsch.getSession(engineNode.getUsername(), engineNode.getHost(), Integer.parseInt(engineNode.getPort()));

        if (engineNode.getPasswd().length() < 1000) {
            session.setPassword(engineNode.getPasswd());
        }

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("ConnectTimeout", "30000");
        config.put("ServerAliveInterval", "30000");
        config.put("ServerAliveCountMax", "3");
        session.setConfig(config);
        session.setTimeout(30000);
        session.connect();

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setPty(pty);
        channel.setCommand(command);
        channel.setInputStream(null);

        InputStream in = channel.getInputStream();
        channel.connect();

        StringBuilder output = new StringBuilder();
        byte[] buffer = new byte[1024];
        long deadline = System.currentTimeMillis() + 10000;

        while (System.currentTimeMillis() < deadline) {
            while (in.available() > 0) {
                int len = in.read(buffer, 0, buffer.length);
                if (len < 0) {
                    break;
                }
                output.append(new String(buffer, 0, len, StandardCharsets.UTF_8));
                if (output.indexOf("\n") >= 0) {
                    String pid = output.toString().split("\\R", 2)[0].trim();
                    channel.disconnect();
                    session.disconnect();
                    if (pid.isEmpty()) {
                        throw new IOException("后台命令执行成功，但未获取到pid");
                    }
                    return pid;
                }
            }

            if (channel.isClosed()) {
                break;
            }
            Thread.sleep(200);
        }

        int exitStatus = channel.getExitStatus();
        String commandOutput = output.toString().trim();
        channel.disconnect();
        session.disconnect();

        if (!commandOutput.isEmpty()) {
            return commandOutput.split("\\R", 2)[0].trim();
        }
        if (exitStatus != 0) {
            throw new IOException("后台命令执行失败，未获取到pid，exitStatus=" + exitStatus);
        }
        throw new IOException("后台命令执行超时，未获取到pid");
    }

    /**
     * scp传递文本.
     */
    public static void scpText(ScpFileEngineNodeDto engineNode, String content, String dstPath)
        throws JSchException, SftpException, InterruptedException {

        // 初始化jsch
        JSch jsch = new JSch();

        if (engineNode.getPasswd().length() > 1000) {
            jsch.addIdentity(engineNode.getUsername(), engineNode.getPasswd().getBytes(), null, null);
        }

        Session session =
            jsch.getSession(engineNode.getUsername(), engineNode.getHost(), Integer.parseInt(engineNode.getPort()));

        // 连接远程服务器
        if (engineNode.getPasswd().length() < 1000) {
            session.setPassword(engineNode.getPasswd());
        }

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("ConnectTimeout", "30000");
        config.put("ServerAliveInterval", "30000");
        config.put("ServerAliveCountMax", "3");
        session.setConfig(config);
        session.setTimeout(30000);
        session.connect();

        // 上传文件
        ChannelSftp channel;
        channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect(120000);
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        channel.put(inputStream, dstPath);

        // 文件校验
        SftpATTRS attrs;
        while (true) {
            attrs = channel.stat(dstPath);
            if (attrs != null) {
                long remoteFileSize = attrs.getSize();
                long localFileSize = content.getBytes().length;
                if (remoteFileSize == localFileSize) {
                    break;
                }
            }
            Thread.sleep(2000);
        }

        channel.disconnect();
        session.disconnect();
    }

    /**
     * scp传递Jar.
     */
    public static void scpJar(ScpFileEngineNodeDto engineNode, String srcPath, String dstPath)
        throws JSchException, SftpException, InterruptedException, IOException {

        // 初始化jsch
        JSch jsch = new JSch();

        if (engineNode.getPasswd().length() > 1000) {
            jsch.addIdentity(engineNode.getUsername(), engineNode.getPasswd().getBytes(), null, null);
        }

        Session session =
            jsch.getSession(engineNode.getUsername(), engineNode.getHost(), Integer.parseInt(engineNode.getPort()));

        // 连接远程服务器
        if (engineNode.getPasswd().length() < 1000) {
            session.setPassword(engineNode.getPasswd());
        }

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("ConnectTimeout", "30000");
        config.put("ServerAliveInterval", "30000");
        config.put("ServerAliveCountMax", "3");
        session.setConfig(config);
        session.setTimeout(30000);
        session.connect();

        // 计算本地文件MD5
        String localMd5 = getLocalFileMd5(srcPath);

        // 通过exec通道获取远程文件MD5
        boolean needUpload = true;
        try {
            String remoteMd5 = getRemoteFileMd5(session, dstPath);
            if (remoteMd5 != null && remoteMd5.equals(localMd5)) {
                log.debug("远程文件已存在且MD5一致，跳过上传: {}", dstPath);
                needUpload = false;
            }
        } catch (Exception e) {
            log.debug("获取远程文件MD5失败，需要上传: {}", dstPath);
        }

        if (needUpload) {
            // 上传文件
            ChannelSftp channel;
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect(120000);
            long localFileSize = Files.size(Paths.get(srcPath));
            channel.put(Files.newInputStream(Paths.get(srcPath)), dstPath);

            // 文件校验
            SftpATTRS attrs;
            while (true) {
                attrs = channel.stat(dstPath);
                if (attrs != null) {
                    long remoteFileSize = attrs.getSize();
                    if (remoteFileSize == localFileSize) {
                        break;
                    }
                }
                Thread.sleep(2000);
            }
            channel.disconnect();
        }

        session.disconnect();
    }

    /**
     * 计算本地文件MD5.
     */
    private static String getLocalFileMd5(String filePath) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            byte[] digest = md.digest(fileBytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("MD5算法不可用", e);
        }
    }

    /**
     * 获取远程文件MD5.
     */
    private static String getRemoteFileMd5(Session session, String remoteFilePath)
        throws JSchException, IOException, InterruptedException {

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand("md5sum " + remoteFilePath);
        channel.setInputStream(null);

        InputStream in = channel.getInputStream();
        channel.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String line;
        StringBuilder output = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }

        while (!channel.isClosed()) {
            Thread.sleep(500);
        }

        int exitStatus = channel.getExitStatus();
        channel.disconnect();

        if (exitStatus != 0 || output.length() == 0) {
            return null;
        }

        // md5sum输出格式: "md5hash filename"
        String result = output.toString().trim();
        if (result.contains(" ")) {
            return result.split("\\s+")[0];
        }
        return null;
    }
}
