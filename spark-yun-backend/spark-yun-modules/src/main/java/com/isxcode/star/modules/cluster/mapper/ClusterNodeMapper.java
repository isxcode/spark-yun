package com.isxcode.star.modules.cluster.mapper;

import com.isxcode.star.api.cluster.pojos.dto.ScpFileEngineNodeDto;
import com.isxcode.star.api.cluster.pojos.req.AddClusterNodeReq;
import com.isxcode.star.api.cluster.pojos.req.UpdateClusterNodeReq;
import com.isxcode.star.api.cluster.pojos.res.EnoQueryNodeRes;
import com.isxcode.star.api.cluster.pojos.res.GetClusterNodeRes;
import com.isxcode.star.modules.cluster.entity.ClusterNodeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** mapstruct映射. */
@Mapper(componentModel = "spring")
public interface ClusterNodeMapper {

	@Mapping(target = "usedMemory", expression = "java(0.0)")
	@Mapping(target = "allMemory", expression = "java(0.0)")
	@Mapping(target = "usedStorage", expression = "java(0.0)")
	@Mapping(target = "allStorage", expression = "java(0.0)")
	@Mapping(target = "cpuPercent", expression = "java(0.0)")
	@Mapping(target = "checkDateTime", expression = "java(java.time.LocalDateTime.now())")
	@Mapping(target = "installSparkLocal", expression = "java(false)")
	ClusterNodeEntity addClusterNodeReqToClusterNodeEntity(AddClusterNodeReq addClusterNodeReq);

	@Mapping(target = "id", source = "clusterNodeEntity.id")
	@Mapping(target = "clusterId", source = "clusterNodeEntity.clusterId")
	@Mapping(target = "name", source = "enoUpdateNodeReq.name")
	@Mapping(target = "remark", source = "enoUpdateNodeReq.remark")
	@Mapping(target = "host", source = "enoUpdateNodeReq.host")
	@Mapping(target = "port", source = "clusterNodeEntity.port")
	@Mapping(target = "username", source = "enoUpdateNodeReq.username")
	@Mapping(target = "passwd", source = "enoUpdateNodeReq.passwd")
	@Mapping(target = "agentHomePath", source = "clusterNodeEntity.agentHomePath")
	@Mapping(target = "agentPort", source = "clusterNodeEntity.agentPort")
	@Mapping(target = "hadoopHomePath", source = "clusterNodeEntity.hadoopHomePath")
	@Mapping(target = "installSparkLocal", source = "enoUpdateNodeReq.installSparkLocal")
	ClusterNodeEntity updateNodeReqToNodeEntity(UpdateClusterNodeReq enoUpdateNodeReq,
			ClusterNodeEntity clusterNodeEntity);

	@Mapping(target = "memory", expression = "java( nodeEntity.getUsedMemory()+ \"G/\" +nodeEntity.getAllMemory()+\"G\")")
	@Mapping(target = "storage", expression = "java( nodeEntity.getUsedStorage()+ \"G/\" +nodeEntity.getAllStorage()+\"G\")")
	@Mapping(target = "cpu", source = "cpuPercent")
	@Mapping(target = "checkDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
	EnoQueryNodeRes nodeEntityToQueryNodeRes(ClusterNodeEntity nodeEntity);

	ScpFileEngineNodeDto engineNodeEntityToScpFileEngineNodeDto(ClusterNodeEntity engineNode);

	GetClusterNodeRes clusterNodeEntityToGetClusterNodeRes(ClusterNodeEntity clusterNode);
}
