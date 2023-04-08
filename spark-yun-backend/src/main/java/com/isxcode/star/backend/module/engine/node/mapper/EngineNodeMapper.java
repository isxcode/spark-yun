package com.isxcode.star.backend.module.engine.node.mapper;

import com.isxcode.star.api.pojos.engine.node.req.EnoAddNodeReq;
import com.isxcode.star.api.pojos.engine.node.res.EnoQueryNodeRes;
import com.isxcode.star.backend.module.engine.node.entity.EngineNodeEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/** mapstruct映射. */
@Mapper(componentModel = "spring")
public interface EngineNodeMapper {

  @Mapping(source = "password", target = "passwd")
  @Mapping(source = "comment", target = "commentInfo")
  @Mapping(target = "usedMemory", expression = "java(0)")
  @Mapping(target = "allMemory", expression = "java(0)")
  @Mapping(target = "usedStorage", expression = "java(0)")
  @Mapping(target = "allStorage", expression = "java(0)")
  @Mapping(target = "cpuPercent", expression = "java(\"0%\")")
  @Mapping(target = "checkDateTime", expression = "java(java.time.LocalDateTime.now())")
  EngineNodeEntity addNodeReqToNodeEntity(EnoAddNodeReq enoAddNodeReq);

  @Mapping(
      target = "memory",
      expression = "java( nodeEntity.getUsedMemory()+ \"/\" +nodeEntity.getAllMemory())")
  @Mapping(
      target = "storage",
      expression = "java( nodeEntity.getUsedStorage()+ \"/\" +nodeEntity.getAllStorage())")
  @Mapping(target = "comment", source = "commentInfo")
  @Mapping(target = "cpu", source = "cpuPercent")
  @Mapping(target = "checkTime", source = "checkDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
  EnoQueryNodeRes nodeEntityToQueryNodeRes(EngineNodeEntity nodeEntity);

  default List<EnoQueryNodeRes> nodeEntityListToQueryNodeResList(
      List<EngineNodeEntity> nodeEntities) {

    return nodeEntities.stream().map(this::nodeEntityToQueryNodeRes).collect(Collectors.toList());
  }

  default Page<EnoQueryNodeRes> datasourceEntityPageToQueryDatasourceResPage(
      Page<EngineNodeEntity> engineNodeEntities) {
    List<EnoQueryNodeRes> dtoList =
        nodeEntityListToQueryNodeResList(engineNodeEntities.getContent());
    return new PageImpl<>(
        dtoList, engineNodeEntities.getPageable(), engineNodeEntities.getTotalElements());
  }
}
