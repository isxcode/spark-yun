package com.isxcode.star.backend.module.datasource.mapper;

import com.isxcode.star.api.pojos.datasource.req.AddDatasourceReq;
import com.isxcode.star.api.pojos.datasource.res.QueryDatasourceRes;
import com.isxcode.star.api.pojos.node.req.AddNodeReq;
import com.isxcode.star.api.pojos.node.res.QueryNodeRes;
import com.isxcode.star.backend.module.datasource.entity.DatasourceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

/** mapstruct映射. */
@Mapper(componentModel = "spring")
public interface DatasourceMapper {

  @Mapping(source = "password", target = "passwd")
  @Mapping(source = "comment", target = "commentInfo")
  DatasourceEntity addDatasourceReqToDatasourceEntity(AddDatasourceReq addDatasourceReq);

  @Mapping(target = "comment", source = "commentInfo")
  @Mapping(target = "checkTime", source = "checkDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
  QueryDatasourceRes datasourceEntityToQueryDatasourceRes(DatasourceEntity nodeEntity);

  default List<QueryDatasourceRes> datasourceEntityListToQueryDatasourceResList(List<DatasourceEntity> nodeEntities) {

    return nodeEntities.stream().map(this::datasourceEntityToQueryDatasourceRes).collect(Collectors.toList());
  }
}
