package com.isxcode.star.backend.module.engine.mapper;

import com.isxcode.star.api.pojos.engine.req.AddEngineReq;
import com.isxcode.star.api.pojos.engine.res.QueryEngineRes;
import com.isxcode.star.backend.module.engine.entity.EngineEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** mapstruct映射. */
@Mapper(componentModel = "spring")
public interface EngineMapper {

  @Mapping(source = "comment", target = "commentInfo")
  EngineEntity addEngineReqToEngineEntity(AddEngineReq addEngineReq);

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
  @Mapping(target = "checkTime", source = "checkDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
  QueryEngineRes engineEntityToQueryEngineRes(EngineEntity engineEntity);

  default List<QueryEngineRes> engineEntityListToQueryEngineResList(
      List<EngineEntity> engineEntityList) {

    return engineEntityList.stream()
        .map(this::engineEntityToQueryEngineRes)
        .collect(Collectors.toList());
  }
}
