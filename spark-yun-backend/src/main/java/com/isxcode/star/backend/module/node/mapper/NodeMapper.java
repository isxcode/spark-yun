package com.isxcode.star.backend.module.node.mapper;

import com.isxcode.star.api.pojos.node.req.AddNodeReq;
import com.isxcode.star.api.pojos.node.res.QueryNodeRes;
import com.isxcode.star.backend.module.node.entity.NodeEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** mapstruct映射. */
@Mapper(componentModel = "spring")
public interface NodeMapper {

  @Mapping(source = "password", target = "passwd")
  @Mapping(source = "comment", target = "commentInfo")
  NodeEntity addNodeReqToNodeEntity(AddNodeReq addNodeReq);

  @Mapping(
      target = "memory",
      expression = "java( nodeEntity.getActiveMemory()+ \"/\" +nodeEntity.getAllMemory())")
  @Mapping(
      target = "storage",
      expression = "java( nodeEntity.getActiveStorage()+ \"/\" +nodeEntity.getAllStorage())")
  @Mapping(target = "comment", source = "commentInfo")
  @Mapping(target = "cpu", source = "cpuPercent")
  @Mapping(target = "checkTime", source = "checkDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
  QueryNodeRes nodeEntityToQueryNodeRes(NodeEntity nodeEntity);

  default List<QueryNodeRes> nodeEntityListToQueryNodeResList(List<NodeEntity> nodeEntities) {

    return nodeEntities.stream().map(this::nodeEntityToQueryNodeRes).collect(Collectors.toList());
  }
}
