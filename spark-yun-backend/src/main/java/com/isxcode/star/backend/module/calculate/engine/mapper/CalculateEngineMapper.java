package com.isxcode.star.backend.module.calculate.engine.mapper;

import com.isxcode.star.api.pojos.calculate.engine.req.CaeAddEngineReq;
import com.isxcode.star.api.pojos.calculate.engine.res.CaeQueryEngineRes;
import com.isxcode.star.backend.module.calculate.engine.entity.CalculateEngineEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/** mapstruct映射. */
@Mapper(componentModel = "spring")
public interface CalculateEngineMapper {

  @Mapping(source = "comment", target = "commentInfo")
  @Mapping(target = "activeNode", expression = "java(0)")
  @Mapping(target = "allNode", expression = "java(0)")
  @Mapping(target = "activeMemory", expression = "java(0)")
  @Mapping(target = "allMemory", expression = "java(0)")
  @Mapping(target = "activeStorage", expression = "java(0)")
  @Mapping(target = "allStorage", expression = "java(0)")
  @Mapping(target = "checkDateTime", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "status", expression = "java(\"UN_CHECK\")")
  CalculateEngineEntity addEngineReqToEngineEntity(CaeAddEngineReq caeAddEngineReq);

  @Mapping(
      target = "node",
      expression = "java( engineEntity.getActiveNode()+ \"/\" +engineEntity.getAllNode())")
  @Mapping(
      target = "memory",
      expression = "java( engineEntity.getActiveMemory()+ \"/\" +engineEntity.getAllMemory())")
  @Mapping(
      target = "storage",
      expression = "java( engineEntity.getActiveStorage()+ \"/\" +engineEntity.getAllStorage())")
  @Mapping(target = "comment", source = "commentInfo")
  @Mapping(target = "checkTime", source = "checkDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
  CaeQueryEngineRes engineEntityToQueryEngineRes(CalculateEngineEntity engineEntity);

  default List<CaeQueryEngineRes> engineEntityListToQueryEngineResList(
      List<CalculateEngineEntity> engineEntityList) {

    return engineEntityList.stream()
        .map(this::engineEntityToQueryEngineRes)
        .collect(Collectors.toList());
  }

  default Page<CaeQueryEngineRes> engineEntityPageToCaeQueryEngineResPage(
      Page<CalculateEngineEntity> engineEntityList) {

    List<CaeQueryEngineRes> dtoList =
        engineEntityListToQueryEngineResList(engineEntityList.getContent());
    return new PageImpl<>(
        dtoList, engineEntityList.getPageable(), engineEntityList.getTotalElements());
  }
}
