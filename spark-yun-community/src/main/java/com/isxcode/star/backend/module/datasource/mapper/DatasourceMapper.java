package com.isxcode.star.backend.module.datasource.mapper;

import com.isxcode.star.api.pojos.datasource.req.DasAddDatasourceReq;
import com.isxcode.star.api.pojos.datasource.req.DasUpdateDatasourceReq;
import com.isxcode.star.api.pojos.datasource.res.DasQueryDatasourceRes;
import com.isxcode.star.backend.module.datasource.entity.DatasourceEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/** mapstruct映射. */
@Mapper(componentModel = "spring")
public interface DatasourceMapper {

  /**
   * dasAddDatasourceReq转DatasourceEntity.
   */
  @Mapping(source = "password", target = "passwd")
  @Mapping(source = "comment", target = "commentInfo")
  @Mapping(source = "type", target = "datasourceType")
  @Mapping(target = "checkDateTime", expression = "java(java.time.LocalDateTime.now())")
  DatasourceEntity dasAddDatasourceReqToDatasourceEntity(DasAddDatasourceReq dasAddDatasourceReq);


  @Mapping(source = "dasUpdateDatasourceReq.password", target = "passwd")
  @Mapping(source = "dasUpdateDatasourceReq.comment", target = "commentInfo")
  @Mapping(source = "dasUpdateDatasourceReq.type", target = "datasourceType")
  @Mapping(source = "dasUpdateDatasourceReq.jdbcUrl", target = "jdbcUrl")
  @Mapping(source = "dasUpdateDatasourceReq.username", target = "username")
  @Mapping(source = "dasUpdateDatasourceReq.name", target = "name")
  @Mapping(target = "id", source = "datasourceEntity.id")
  DatasourceEntity dasUpdateDatasourceReqToDatasourceEntity(DasUpdateDatasourceReq dasUpdateDatasourceReq,DatasourceEntity datasourceEntity);

  /**
   * datasourceEntity转DasQueryDatasourceRes.
   */
  @Mapping(target = "comment", source = "commentInfo")
  @Mapping(target = "type", source = "datasourceType")
  @Mapping(target = "checkTime", source = "checkDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
  DasQueryDatasourceRes datasourceEntityToQueryDatasourceRes(DatasourceEntity datasourceEntity);

  /** List[datasourceEntity]转List[DasQueryDatasourceRes]. */
  List<DasQueryDatasourceRes> datasourceEntityToQueryDatasourceRes(
      List<DatasourceEntity> datasourceEntity);

  /** Page[datasourceEntity]转Page[DasQueryDatasourceRes]. */
  default Page<DasQueryDatasourceRes> datasourceEntityListToQueryDatasourceResList(
      Page<DatasourceEntity> pageDatasource) {
    List<DasQueryDatasourceRes> dtoList =
        datasourceEntityToQueryDatasourceRes(pageDatasource.getContent());
    return new PageImpl<>(dtoList, pageDatasource.getPageable(), pageDatasource.getTotalElements());
  }
}
