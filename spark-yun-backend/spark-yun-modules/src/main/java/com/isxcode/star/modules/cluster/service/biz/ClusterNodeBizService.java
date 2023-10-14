package com.isxcode.star.modules.cluster.service.biz;

import static com.isxcode.star.common.config.CommonConfig.TENANT_ID;
import static com.isxcode.star.common.config.CommonConfig.USER_ID;

import com.isxcode.star.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.star.api.cluster.constants.ClusterStatus;
import com.isxcode.star.api.cluster.pojos.dto.ScpFileEngineNodeDto;
import com.isxcode.star.api.cluster.pojos.req.*;
import com.isxcode.star.api.cluster.pojos.res.EnoQueryNodeRes;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.common.utils.AesUtils;
import com.isxcode.star.modules.cluster.entity.ClusterEntity;
import com.isxcode.star.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.star.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.star.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.star.modules.cluster.repository.ClusterRepository;
import com.isxcode.star.modules.cluster.run.*;
import com.isxcode.star.modules.cluster.service.ClusterNodeService;
import com.isxcode.star.modules.cluster.service.ClusterService;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 用户模块接口的业务逻辑. */
@Service
@RequiredArgsConstructor
@Transactional(noRollbackFor = {IsxAppException.class})
@Slf4j
public class ClusterNodeBizService {

	private final ClusterNodeRepository engineNodeRepository;

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

		clusterService.checkCluster(addClusterNodeReq.getClusterId());

		ClusterNodeEntity clusterNode = engineNodeMapper.addClusterNodeReqToClusterNodeEntity(addClusterNodeReq);

		// 密码对成加密
		clusterNode.setPasswd(aesUtils.encrypt(addClusterNodeReq.getPasswd().trim()));

		// 设置服务器默认端口号
		clusterNode.setPort(Strings.isEmpty(addClusterNodeReq.getPort()) ? "22" : addClusterNodeReq.getPort().trim());

		// 设置默认代理安装地址
		clusterNode.setAgentHomePath(clusterNodeService.getDefaultAgentHomePath(addClusterNodeReq.getAgentHomePath(),
				addClusterNodeReq.getUsername().trim()));

		// 添加特殊逻辑，从备注中获取安装路径
		// 正则获取路径 $path{/root}
		if (!Strings.isEmpty(addClusterNodeReq.getRemark())) {

			Pattern regex = Pattern.compile("\\$path\\{(.*?)\\}");
			Matcher matcher = regex.matcher(addClusterNodeReq.getRemark());
			if (matcher.find()) {
				clusterNode.setAgentHomePath(matcher.group(1));
			}
		}

		// 设置默认代理端口号
		clusterNode.setAgentPort(clusterNodeService.getDefaultAgentPort(addClusterNodeReq.getAgentPort().trim()));

		// 初始化节点状态，未检测
		clusterNode.setStatus(ClusterNodeStatus.UN_INSTALL);

		// 持久化数据
		engineNodeRepository.save(clusterNode);
	}

	public void updateClusterNode(UpdateClusterNodeReq updateClusterNodeReq) {

		ClusterEntity cluster = clusterService.getCluster(updateClusterNodeReq.getClusterId());

		ClusterNodeEntity clusterNode = clusterNodeService.getClusterNode(updateClusterNodeReq.getId());

		// 转换对象
		ClusterNodeEntity node = engineNodeMapper.updateNodeReqToNodeEntity(updateClusterNodeReq, clusterNode);

		// 设置安装地址
		node.setAgentHomePath(clusterNodeService.getDefaultAgentHomePath(updateClusterNodeReq.getAgentHomePath(),
				updateClusterNodeReq.getUsername()));

		// 添加特殊逻辑，从备注中获取安装路径
		// 正则获取路径 $path{/root}
		if (!Strings.isEmpty(updateClusterNodeReq.getRemark())) {

			Pattern regex = Pattern.compile("\\$path\\{(.*?)\\}");
			Matcher matcher = regex.matcher(updateClusterNodeReq.getRemark());
			if (matcher.find()) {
				node.setAgentHomePath(matcher.group(1));
			}
		}

		// 密码对成加密
		node.setPasswd(aesUtils.encrypt(updateClusterNodeReq.getPasswd()));

		// 设置代理端口号
		node.setAgentPort(clusterNodeService.getDefaultAgentPort(updateClusterNodeReq.getAgentPort()));

		// 初始化节点状态，未检测
		node.setStatus(ClusterNodeStatus.UN_CHECK);
		engineNodeRepository.save(node);

		// 集群状态修改
		cluster.setStatus(ClusterStatus.UN_CHECK);
		clusterRepository.save(cluster);
	}

	public Page<EnoQueryNodeRes> pageClusterNode(PageClusterNodeReq enoQueryNodeReq) {

		Page<ClusterNodeEntity> engineNodeEntities = engineNodeRepository.searchAll(enoQueryNodeReq.getSearchKeyWord(),
				enoQueryNodeReq.getClusterId(),
				PageRequest.of(enoQueryNodeReq.getPage(), enoQueryNodeReq.getPageSize()));

		return engineNodeMapper.datasourceEntityPageToQueryDatasourceResPage(engineNodeEntities);
	}

	public void deleteClusterNode(DeleteClusterNodeReq deleteClusterNodeReq) {

		Optional<ClusterNodeEntity> engineNodeEntityOptional = engineNodeRepository
				.findById(deleteClusterNodeReq.getEngineNodeId());
		if (!engineNodeEntityOptional.isPresent()) {
			throw new IsxAppException("节点已删除");
		}

		// 判断节点状态是否为已安装
		if (ClusterNodeStatus.RUNNING.equals(engineNodeEntityOptional.get().getStatus())) {
			throw new IsxAppException("请卸载节点后删除");
		}

		engineNodeRepository.deleteById(deleteClusterNodeReq.getEngineNodeId());
	}

	public void checkAgent(CheckAgentReq checkAgentReq) {

		// 获取节点信息
		ClusterNodeEntity engineNode = clusterNodeService.getClusterNode(checkAgentReq.getEngineNodeId());

		// 如果是安装中等状态，需要等待运行结束
		if (ClusterNodeStatus.CHECKING.equals(engineNode.getStatus())
				|| ClusterNodeStatus.INSTALLING.equals(engineNode.getStatus())
				|| ClusterNodeStatus.REMOVING.equals(engineNode.getStatus())) {
			throw new IsxAppException("进行中，稍后再试");
		}

		// 转换请求节点检测对象
		ScpFileEngineNodeDto scpFileEngineNodeDto = engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
		scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

		// 修改状态
		engineNode.setStatus(ClusterNodeStatus.CHECKING);
		engineNode.setAgentLog("检测中");

		// 持久化
		engineNodeRepository.saveAndFlush(engineNode);

		// 异步调用
		runAgentCheckService.run(checkAgentReq.getEngineNodeId(), scpFileEngineNodeDto, TENANT_ID.get(), USER_ID.get());
	}

	/** 安装节点. */
	public void installAgent(InstallAgentReq installAgentReq) {

		ClusterNodeEntity clusterNode = clusterNodeService.getClusterNode(installAgentReq.getEngineNodeId());

		ClusterEntity cluster = clusterService.getCluster(clusterNode.getClusterId());

		// 如果是安装中等状态，需要等待运行结束
		if (ClusterNodeStatus.CHECKING.equals(clusterNode.getStatus())
				|| ClusterNodeStatus.INSTALLING.equals(clusterNode.getStatus())
				|| ClusterNodeStatus.REMOVING.equals(clusterNode.getStatus())) {
			throw new IsxAppException("进行中，稍后再试");
		}

		// 将节点信息转成工具类识别对象
		ScpFileEngineNodeDto scpFileEngineNodeDto = engineNodeMapper
				.engineNodeEntityToScpFileEngineNodeDto(clusterNode);
		scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

		// 修改状态
		clusterNode.setStatus(ClusterNodeStatus.INSTALLING);
		clusterNode.setAgentLog("激活中");

		// 持久化
		engineNodeRepository.saveAndFlush(clusterNode);

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
			throw new IsxAppException("进行中，稍后再试");
		}

		// 将节点信息转成工具类识别对象
		ScpFileEngineNodeDto scpFileEngineNodeDto = engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
		scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

		// 修改状态
		engineNode.setStatus(ClusterNodeStatus.REMOVING);
		engineNode.setAgentLog("卸载中");

		// 持久化
		engineNodeRepository.saveAndFlush(engineNode);

		// 异步调用
		runAgentRemoveService.run(removeAgentReq.getEngineNodeId(), scpFileEngineNodeDto, TENANT_ID.get(),
				USER_ID.get());
	}

	public void cleanAgent(CleanAgentReq cleanAgentReq) {

		// 获取节点信息
		ClusterNodeEntity engineNode = clusterNodeService.getClusterNode(cleanAgentReq.getEngineNodeId());

		// 将节点信息转成工具类识别对象
		ScpFileEngineNodeDto scpFileEngineNodeDto = engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
		scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

		// 同步调用
		runAgentCleanService.run(cleanAgentReq.getEngineNodeId(), scpFileEngineNodeDto, TENANT_ID.get(), USER_ID.get());
	}

	/** 停止节点. */
	public void stopAgent(StopAgentReq stopAgentReq) {

		// 获取节点信息
		ClusterNodeEntity engineNode = clusterNodeService.getClusterNode(stopAgentReq.getEngineNodeId());

		// 如果是安装中等状态，需要等待运行结束
		if (ClusterNodeStatus.CHECKING.equals(engineNode.getStatus())
				|| ClusterNodeStatus.INSTALLING.equals(engineNode.getStatus())
				|| ClusterNodeStatus.REMOVING.equals(engineNode.getStatus())) {
			throw new IsxAppException("进行中，稍后再试");
		}

		// 将节点信息转成工具类识别对象
		ScpFileEngineNodeDto scpFileEngineNodeDto = engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
		scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

		// 修改状态
		engineNode.setStatus(ClusterNodeStatus.STOPPING);
		engineNode.setAgentLog("停止中");

		// 持久化
		engineNodeRepository.saveAndFlush(engineNode);

		// 异步调用
		runAgentStopService.run(stopAgentReq.getEngineNodeId(), scpFileEngineNodeDto, TENANT_ID.get(), USER_ID.get());
	}

	/** 激活中. */
	public void startAgent(StartAgentReq startAgentReq) {

		// 获取节点信息
		ClusterNodeEntity engineNode = clusterNodeService.getClusterNode(startAgentReq.getEngineNodeId());

		// 如果是安装中等状态，需要等待运行结束
		if (ClusterNodeStatus.CHECKING.equals(engineNode.getStatus())
				|| ClusterNodeStatus.INSTALLING.equals(engineNode.getStatus())
				|| ClusterNodeStatus.REMOVING.equals(engineNode.getStatus())) {
			throw new IsxAppException("进行中，稍后再试");
		}

		// 将节点信息转成工具类识别对象
		ScpFileEngineNodeDto scpFileEngineNodeDto = engineNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
		scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

		// 修改状态
		engineNode.setStatus(ClusterNodeStatus.STARTING);
		engineNode.setAgentLog("启动中");

		// 持久化
		engineNodeRepository.saveAndFlush(engineNode);

		// 异步调用
		runAgentStartService.run(startAgentReq.getEngineNodeId(), scpFileEngineNodeDto, TENANT_ID.get(), USER_ID.get());
	}
}
