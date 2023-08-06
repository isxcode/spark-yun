package com.isxcode.star.modules.datasource.mapper;

import com.isxcode.star.api.datasource.pojos.req.DasAddDatasourceReq;
import com.isxcode.star.api.datasource.pojos.req.DasUpdateDatasourceReq;
import com.isxcode.star.api.datasource.pojos.res.DasQueryDatasourceRes;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/** mapstruct映射. */
@Mapper(componentModel = "spring")
public interface DatasourceMapper {

  /** dasAddDatasourceReq转DatasourceEntity. */
  DatasourceEntity dasAddDatasourceReqToDatasourceEntity(DasAddDatasourceReq dasAddDatasourceReq);

  @Mapping(source = "dasUpdateDatasourceReq.passwd", target = "passwd")
  @Mapping(source = "dasUpdateDatasourceReq.remark", target = "remark")
  @Mapping(source = "dasUpdateDatasourceReq.dbType", target = "dbType")
  @Mapping(source = "dasUpdateDatasourceReq.jdbcUrl", target = "jdbcUrl")
  @Mapping(source = "dasUpdateDatasourceReq.username", target = "username")
  @Mapping(source = "dasUpdateDatasourceReq.name", target = "name")
  @Mapping(target = "id", source = "datasourceEntity.id")
  DatasourceEntity dasUpdateDatasourceReqToDatasourceEntity(
      DasUpdateDatasourceReq dasUpdateDatasourceReq, DatasourceEntity datasourceEntity);

  @Mapping(target = "checkDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
  DasQueryDatasourceRes datasourceEntityToQueryDatasourceRes(DatasourceEntity datasourceEntity);

  List<DasQueryDatasourceRes> datasourceEntityToQueryDatasourceResList(
      List<DatasourceEntity> datasourceEntity);

  default Page<DasQueryDatasourceRes> datasourceEntityToQueryDatasourceResPage(
      Page<DatasourceEntity> pageDatasource) {
    List<DasQueryDatasourceRes> dtoList =
        datasourceEntityToQueryDatasourceResList(pageDatasource.getContent());
    return new PageImpl<>(dtoList, pageDatasource.getPageable(), pageDatasource.getTotalElements());
  }
}
