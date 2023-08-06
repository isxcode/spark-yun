package com.isxcode.star.modules.cluster.mapper;

import com.isxcode.star.api.cluster.pojos.req.CaeAddEngineReq;
import com.isxcode.star.api.cluster.pojos.req.CaeUpdateEngineReq;
import com.isxcode.star.api.cluster.pojos.res.CaeQueryEngineRes;
import com.isxcode.star.modules.cluster.entity.ClusterEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/** mapstruct映射. */
@Mapper(componentModel = "spring")
public interface ClusterMapper {

  @Mapping(target = "activeNodeNum", expression = "java(0)")
  @Mapping(target = "allNodeNum", expression = "java(0)")
  @Mapping(target = "usedMemoryNum", expression = "java(0.0)")
  @Mapping(target = "allMemoryNum", expression = "java(0.0)")
  @Mapping(target = "usedStorageNum", expression = "java(0.0)")
  @Mapping(target = "allStorageNum", expression = "java(0.0)")
  @Mapping(target = "checkDateTime", expression = "java(java.time.LocalDateTime.now())")
  ClusterEntity addEngineReqToEngineEntity(CaeAddEngineReq caeAddEngineReq);

  @Mapping(target = "name", source = "caeUpdateEngineReq.name")
  @Mapping(target = "remark", source = "caeUpdateEngineReq.remark")
  ClusterEntity updateEngineReqToEngineEntity(
      CaeUpdateEngineReq caeUpdateEngineReq, ClusterEntity calculateEngineEntity);

  @Mapping(
      target = "node",
      expression = "java( engineEntity.getActiveNodeNum()+ \"/\" +engineEntity.getAllNodeNum())")
  @Mapping(
      target = "memory",
      expression =
          "java( engineEntity.getUsedMemoryNum()+ \"G/\" +engineEntity.getAllMemoryNum()+\"G\")")
  @Mapping(
      target = "storage",
      expression =
          "java( engineEntity.getUsedStorageNum()+ \"G/\"  +engineEntity.getAllStorageNum()+\"G\")")
  @Mapping(target = "checkDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
  CaeQueryEngineRes engineEntityToQueryEngineRes(ClusterEntity engineEntity);

  default List<CaeQueryEngineRes> engineEntityListToQueryEngineResList(
      List<ClusterEntity> engineEntityList) {

    return engineEntityList.stream()
        .map(this::engineEntityToQueryEngineRes)
        .collect(Collectors.toList());
  }

  default Page<CaeQueryEngineRes> engineEntityPageToCaeQueryEngineResPage(
      Page<ClusterEntity> engineEntityList) {

    List<CaeQueryEngineRes> dtoList =
        engineEntityListToQueryEngineResList(engineEntityList.getContent());
    return new PageImpl<>(
        dtoList, engineEntityList.getPageable(), engineEntityList.getTotalElements());
  }
}
