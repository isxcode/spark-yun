package com.isxcode.star.modules.cluster.service.biz;

import static com.isxcode.star.common.config.CommonConfig.TENANT_ID;
import static com.isxcode.star.common.config.CommonConfig.USER_ID;

import com.isxcode.star.api.agent.constants.AgentType;
import com.isxcode.star.api.api.constants.PathConstants;
import com.isxcode.star.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.star.api.cluster.constants.ClusterStatus;
import com.isxcode.star.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.star.api.cluster.req.*;
import com.isxcode.star.api.cluster.res.QueryNodeRes;
import com.isxcode.star.api.cluster.res.GetClusterNodeRes;
import com.isxcode.star.api.cluster.res.TestAgentRes;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.common.utils.aes.AesUtils;
import com.isxcode.star.common.utils.ssh.SshUtils;
import com.isxcode.star.modules.cluster.entity.ClusterEntity;
import com.isxcode.star.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.star.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.star.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.star.modules.cluster.repository.ClusterRepository;
import com.isxcode.star.modules.cluster.run.*;
import com.isxcode.star.modules.cluster.service.ClusterNodeService;
import com.isxcode.star.modules.cluster.service.ClusterService;

import java.io.IOException;

import com.jcraft.jsch.JSchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(noRollbackFor = {IsxAppException.class})
@Slf4j
public class ClusterNodeBizService {

    private final ClusterNodeRepository clusterNodeRepository;

    private final ClusterRepository clusterRepository;

    private final ClusterService clusterService;

    private final ClusterNodeMapper engineNodeMapper;

    private final RunAgentCheckService runAgentCheckService;

    private final RunAgentInstallService runAgentInstallService;

    private final RunAgentStopService runAgentStopService;

    private final RunAgentStartService runAgentStartService;

    private final RunAgentRemoveService runAgentRemoveService;

    private final RunAgentCleanService runAgentCleanService;

    private final AesUtils aesUtils;

    private final ClusterNodeService clusterNodeService;

    public void addClusterNode(AddClusterNodeReq addClusterNodeReq) {

        ClusterEntity cluster = clusterService.getCluster(addClusterNodeReq.getClusterId());

        ClusterNodeEntity clusterNode = engineNodeMapper.addClusterNodeReqToClusterNodeEntity(addClusterNodeReq);

        // 是否安装spark-local组件
        clusterNode.setInstallSparkLocal(addClusterNodeReq.getInstallSparkLocal());

        // 密码对成加密
        clusterNode.setPasswd(aesUtils.encrypt(addClusterNodeReq.getPasswd().trim()));

        // 设置服务器默认端口号
        clusterNode.setPort(Strings.isEmpty(addClusterNodeReq.getPort()) ? "22" : addClusterNodeReq.getPort().trim());

        // 设置默认代理端口号
        clusterNode.setAgentPort(clusterNodeService.getDefaultAgentPort(addClusterNodeReq.getAgentPort().trim()));

        // 初始化节点状态，未检测
        clusterNode.setStatus(ClusterNodeStatus.UN_INSTALL);

        // 设置默认代理安装地址
        clusterNode.setAgentHomePath(
            clusterNodeService.getDefaultAgentHomePath(addClusterNodeReq.getUsername().trim(), clusterNode));

        // 如果是默认安装spark,设置默认路径
        if (addClusterNodeReq.getInstallSparkLocal() || !AgentType.StandAlone.equals(cluster.getClusterType())) {
            clusterNode.setSparkHomePath(clusterNode.getAgentHomePath() + "/" + PathConstants.AGENT_PATH_NAME + "/"
                + PathConstants.SPARK_MIN_HOME);
        } else {
            clusterNode.setSparkHomePath(addClusterNodeReq.getSparkHomePath());
        }

        // 持久化数据
        clusterNodeRepository.save(clusterNode);
    }

    public void updateClusterNode(UpdateClusterNodeReq updateClusterNodeReq) {

        ClusterEntity cluster = clusterService.getCluster(updateClusterNodeReq.getClusterId());

        ClusterNodeEntity clusterNode = clusterNodeService.getClusterNode(updateClusterNodeReq.getId());

        // 如果是安装中等状态，需要等待运行结束
        if (ClusterNodeStatus.CHECKING.equals(clusterNode.getStatus())
            || ClusterNodeStatus.INSTALLING.equals(clusterNode.getStatus())
            || ClusterNodeStatus.REMOVING.equals(clusterNode.getStatus())
            || ClusterNodeStatus.STARTING.equals(clusterNode.getStatus())
            || ClusterNodeStatus.STOPPING.equals(clusterNode.getStatus())) {
            throw new IsxAppException("当前状态无法操作，请稍后再试");
        }

        // 转换对象
        clusterNode = engineNodeMapper.updateNodeReqToNodeEntity(updateClusterNodeReq, clusterNode);

        // 是否安装spark-local组件
        clusterNode.setInstallSparkLocal(updateClusterNodeReq.getInstallSparkLocal());

        // 密码对成加密
        clusterNode.setPasswd(aesUtils.encrypt(updateClusterNodeReq.getPasswd().trim()));

        // 设置安装地址
        clusterNode.setAgentHomePath(
            clusterNodeService.getDefaultAgentHomePath(updateClusterNodeReq.getUsername(), clusterNode));

        // 如果是默认安装spark,设置默认路径
        if (updateClusterNodeReq.getInstallSparkLocal() || !AgentType.StandAlone.equals(cluster.getClusterType())) {
            clusterNode.setSparkHomePath(clusterNode.getAgentHomePath() + "/" + PathConstants.AGENT_PATH_NAME + "/"
                + PathConstants.SPARK_MIN_HOME);
        } else {
            clusterNode.setSparkHomePath(updateClusterNodeReq.getSparkHomePath());
        }

        // 设置代理端口号
        clusterNode.setAgentPort(clusterNodeService.getDefaultAgentPort(updateClusterNodeReq.getAgentPort()));

        // 初始化节点状态，未检测
        clusterNode.setStatus(ClusterNodeStatus.UN_CHECK);
        clusterNodeRepository.save(clusterNode);

        // 集群状态修改
        cluster.setStatus(ClusterStatus.UN_CHECK);
        clusterRepository.save(cluster);
    }

    public Page<QueryNodeRes> pageClusterNode(PageClusterNodeReq enoQueryNodeReq) {

        Page<ClusterNodeEntity> engineNodeEntities = clusterNodeRepository.searchAll(enoQueryNodeReq.getSearchKeyWord(),
            enoQueryNodeReq.getClusterId(), PageRequest.of(enoQueryNodeReq.getPage(), enoQueryNodeReq.getPageSize()));

        return engineNodeEntities.map(engineNodeMapper::nodeEntityToQueryNodeRes);
    }

    public void deleteClusterNode(DeleteClusterNodeReq deleteClusterNodeReq) {

        ClusterNodeEntity clusterNode = clusterNodeRepository.findById(deleteClusterNodeReq.getEngineNodeId())
            .orElseThrow(() -> new IsxAppException("节点已删除"));

        // 如果是安装中等状态，需要等待运行结束
        if (ClusterNodeStatus.CHECKING.equals(clusterNode.getStatus())
            || ClusterNodeStatus.INSTALLING.equals(clusterNode.getStatus())
            || ClusterNodeStatus.REMOVING.equals(clusterNode.getStatus())
            || ClusterNodeStatus.STARTING.equals(clusterNode.getStatus())
            || ClusterNodeStatus.STOPPING.equals(clusterNode.getStatus())
            || ClusterNodeStatus.RUNNING.equals(clusterNode.getStatus())) {
            throw new IsxAppException("请卸载节点后删除");
        }

        clusterNodeRepository.deleteById(deleteClusterNodeReq.getEngineNodeId());
    }

    public void checkAgent(CheckAgentReq checkAgentReq) {

        // 获取节点信息
        ClusterNodeEntity engineNode = clusterNodeService.getClusterNode(checkAgentReq.getEngineNodeId());

        // 如果是安装中等状态，需要等待运行结束
        if (ClusterNodeStatus.CHECKING.equals(engineNode.getStatus())
            || ClusterNodeStatus.INSTALLING.equals(engineNode.getStatus())
            || ClusterNodeStatus.REMOVING.equals(engineNode.getStatus())
            || ClusterNodeStatus.STARTING.equals(engineNode.getStatus())
            || ClusterNodeStatus.STOPPING.equals(engineNode.getStatus())) {
            throw new IsxAppException("当前状态无法操作，请稍后再试");
        }

        // 转换请求节点检测对象
        ScpFileEngineNodeDto scpFileEngineNodeDto = engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
        scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

        // 修改状态
        engineNode.setStatus(ClusterNodeStatus.CHECKING);
        engineNode.setAgentLog("检测中");

        // 持久化
        clusterNodeRepository.saveAndFlush(engineNode);

        // 异步调用
        runAgentCheckService.run(checkAgentReq.getEngineNodeId(), scpFileEngineNodeDto, TENANT_ID.get(), USER_ID.get());
    }

    public TestAgentRes testAgent(TestAgentReq testAgentReq) {

        ScpFileEngineNodeDto scpFileEngineNodeDto = ScpFileEngineNodeDto.builder().host(testAgentReq.getHost())
            .port(testAgentReq.getPort()).passwd(testAgentReq.getPasswd()).username(testAgentReq.getUsername()).build();
        String testAgent = "echo 'hello'";
        try {
            String testBack = SshUtils.executeCommand(scpFileEngineNodeDto, testAgent, false);
            if ("hello\n".equals(testBack)) {
                return TestAgentRes.builder().status("SUCCESS").log("链接成功").build();
            } else {
                return TestAgentRes.builder().status("FAIL").log(testBack).build();
            }
        } catch (JSchException | InterruptedException | IOException e) {
            return TestAgentRes.builder().status("FAIL").log(e.getMessage()).build();
        }
    }

    /**
     * 安装节点.
     */
    public void installAgent(InstallAgentReq installAgentReq) {

        ClusterNodeEntity clusterNode = clusterNodeService.getClusterNode(installAgentReq.getEngineNodeId());

        ClusterEntity cluster = clusterService.getCluster(clusterNode.getClusterId());

        // 如果是安装中等状态，需要等待运行结束
        if (ClusterNodeStatus.CHECKING.equals(clusterNode.getStatus())
            || ClusterNodeStatus.INSTALLING.equals(clusterNode.getStatus())
            || ClusterNodeStatus.REMOVING.equals(clusterNode.getStatus())
            || ClusterNodeStatus.STARTING.equals(clusterNode.getStatus())
            || ClusterNodeStatus.STOPPING.equals(clusterNode.getStatus())
            || ClusterNodeStatus.RUNNING.equals(clusterNode.getStatus())) {
            throw new IsxAppException("当前状态无法操作，请稍后再试");
        }

        // 将节点信息转成工具类识别对象
        ScpFileEngineNodeDto scpFileEngineNodeDto =
            engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(clusterNode);
        scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

        // 修改状态
        clusterNode.setStatus(ClusterNodeStatus.INSTALLING);
        clusterNode.setAgentLog("激活中");

        // 持久化
        clusterNodeRepository.saveAndFlush(clusterNode);

        // 异步调用
        runAgentInstallService.run(installAgentReq.getEngineNodeId(), cluster.getClusterType(), scpFileEngineNodeDto,
            TENANT_ID.get(), USER_ID.get());
    }

    public void removeAgent(RemoveAgentReq removeAgentReq) {

        // 获取节点信息
        ClusterNodeEntity engineNode = clusterNodeService.getClusterNode(removeAgentReq.getEngineNodeId());

        // 如果是安装中等状态，需要等待运行结束
        if (ClusterNodeStatus.CHECKING.equals(engineNode.getStatus())
            || ClusterNodeStatus.INSTALLING.equals(engineNode.getStatus())
            || ClusterNodeStatus.REMOVING.equals(engineNode.getStatus())) {
            throw new IsxAppException("当前状态无法操作，请稍后再试");
        }

        // 将节点信息转成工具类识别对象
        ScpFileEngineNodeDto scpFileEngineNodeDto = engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
        scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

        // 修改状态
        engineNode.setStatus(ClusterNodeStatus.REMOVING);
        engineNode.setAgentLog("卸载中");

        // 持久化
        clusterNodeRepository.saveAndFlush(engineNode);

        // 异步调用
        runAgentRemoveService.run(removeAgentReq.getEngineNodeId(), scpFileEngineNodeDto, TENANT_ID.get(),
            USER_ID.get());
    }

    public void cleanAgent(CleanAgentReq cleanAgentReq) {

        // 获取节点信息
        ClusterNodeEntity engineNode = clusterNodeService.getClusterNode(cleanAgentReq.getEngineNodeId());

        // 如果是安装中等状态，需要等待运行结束
        if (ClusterNodeStatus.CHECKING.equals(engineNode.getStatus())
            || ClusterNodeStatus.INSTALLING.equals(engineNode.getStatus())
            || ClusterNodeStatus.REMOVING.equals(engineNode.getStatus())
            || ClusterNodeStatus.STARTING.equals(engineNode.getStatus())
            || ClusterNodeStatus.STOPPING.equals(engineNode.getStatus())) {
            throw new IsxAppException("当前状态无法操作，请稍后再试");
        }

        // 将节点信息转成工具类识别对象
        ScpFileEngineNodeDto scpFileEngineNodeDto = engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
        scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

        // 同步调用
        runAgentCleanService.run(cleanAgentReq.getEngineNodeId(), scpFileEngineNodeDto, TENANT_ID.get(), USER_ID.get());
    }

    /**
     * 停止节点.
     */
    public void stopAgent(StopAgentReq stopAgentReq) {

        // 获取节点信息
        ClusterNodeEntity engineNode = clusterNodeService.getClusterNode(stopAgentReq.getEngineNodeId());

        // 如果是安装中等状态，需要等待运行结束
        if (ClusterNodeStatus.CHECKING.equals(engineNode.getStatus())
            || ClusterNodeStatus.INSTALLING.equals(engineNode.getStatus())
            || ClusterNodeStatus.REMOVING.equals(engineNode.getStatus())
            || ClusterNodeStatus.STARTING.equals(engineNode.getStatus())
            || ClusterNodeStatus.STOPPING.equals(engineNode.getStatus())) {
            throw new IsxAppException("当前状态无法操作，请稍后再试");
        }

        // 将节点信息转成工具类识别对象
        ScpFileEngineNodeDto scpFileEngineNodeDto = engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
        scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

        // 修改状态
        engineNode.setStatus(ClusterNodeStatus.STOPPING);
        engineNode.setAgentLog("停止中");

        // 持久化
        clusterNodeRepository.saveAndFlush(engineNode);

        // 异步调用
        runAgentStopService.run(stopAgentReq.getEngineNodeId(), scpFileEngineNodeDto, TENANT_ID.get(), USER_ID.get());
    }

    /**
     * 激活中.
     */
    public void startAgent(StartAgentReq startAgentReq) {

        // 获取节点信息
        ClusterNodeEntity engineNode = clusterNodeService.getClusterNode(startAgentReq.getEngineNodeId());

        // 如果是安装中等状态，需要等待运行结束
        if (ClusterNodeStatus.CHECKING.equals(engineNode.getStatus())
            || ClusterNodeStatus.INSTALLING.equals(engineNode.getStatus())
            || ClusterNodeStatus.REMOVING.equals(engineNode.getStatus())
            || ClusterNodeStatus.STARTING.equals(engineNode.getStatus())
            || ClusterNodeStatus.STOPPING.equals(engineNode.getStatus())) {
            throw new IsxAppException("当前状态无法操作，请稍后再试");
        }

        // 将节点信息转成工具类识别对象
        ScpFileEngineNodeDto scpFileEngineNodeDto = engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
        scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

        // 修改状态
        engineNode.setStatus(ClusterNodeStatus.STARTING);
        engineNode.setAgentLog("启动中");

        // 持久化
        clusterNodeRepository.saveAndFlush(engineNode);

        // 异步调用
        runAgentStartService.run(startAgentReq.getEngineNodeId(), scpFileEngineNodeDto, TENANT_ID.get(), USER_ID.get());
    }

    public GetClusterNodeRes getClusterNode(GetClusterNodeReq getClusterNodeReq) {

        ClusterNodeEntity clusterNode = clusterNodeService.getClusterNode(getClusterNodeReq.getClusterNodeId());
        return engineNodeMapper.clusterNodeEntityToGetClusterNodeRes(clusterNode);
    }
}
