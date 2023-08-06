package com.isxcode.star.modules.cluster.mapper;

import com.isxcode.star.api.cluster.pojos.dto.ScpFileEngineNodeDto;
import com.isxcode.star.api.cluster.pojos.req.EnoAddNodeReq;
import com.isxcode.star.api.cluster.pojos.req.EnoUpdateNodeReq;
import com.isxcode.star.api.cluster.pojos.res.EnoQueryNodeRes;
import com.isxcode.star.modules.cluster.entity.ClusterNodeEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/** mapstruct映射. */
@Mapper(componentModel = "spring")
public interface ClusterNodeMapper {

  @Mapping(target = "usedMemory", expression = "java(0.0)")
  @Mapping(target = "allMemory", expression = "java(0.0)")
  @Mapping(target = "usedStorage", expression = "java(0.0)")
  @Mapping(target = "allStorage", expression = "java(0.0)")
  @Mapping(target = "cpuPercent", expression = "java(0.0)")
  @Mapping(target = "checkDateTime", expression = "java(java.time.LocalDateTime.now())")
  ClusterNodeEntity addNodeReqToNodeEntity(EnoAddNodeReq enoAddNodeReq);

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
  ClusterNodeEntity updateNodeReqToNodeEntity(
      EnoUpdateNodeReq enoUpdateNodeReq, ClusterNodeEntity clusterNodeEntity);

  @Mapping(
      target = "memory",
      expression = "java( nodeEntity.getUsedMemory()+ \"G/\" +nodeEntity.getAllMemory()+\"G\")")
  @Mapping(
      target = "storage",
      expression = "java( nodeEntity.getUsedStorage()+ \"G/\" +nodeEntity.getAllStorage()+\"G\")")
  @Mapping(target = "cpu", source = "cpuPercent")
  @Mapping(target = "checkDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
  EnoQueryNodeRes nodeEntityToQueryNodeRes(ClusterNodeEntity nodeEntity);

  default List<EnoQueryNodeRes> nodeEntityListToQueryNodeResList(
      List<ClusterNodeEntity> nodeEntities) {

    return nodeEntities.stream().map(this::nodeEntityToQueryNodeRes).collect(Collectors.toList());
  }

  default Page<EnoQueryNodeRes> datasourceEntityPageToQueryDatasourceResPage(
      Page<ClusterNodeEntity> engineNodeEntities) {
    List<EnoQueryNodeRes> dtoList =
        nodeEntityListToQueryNodeResList(engineNodeEntities.getContent());
    return new PageImpl<>(
        dtoList, engineNodeEntities.getPageable(), engineNodeEntities.getTotalElements());
  }

  ScpFileEngineNodeDto engineNodeEntityToScpFileEngineNodeDto(ClusterNodeEntity engineNode);
}
