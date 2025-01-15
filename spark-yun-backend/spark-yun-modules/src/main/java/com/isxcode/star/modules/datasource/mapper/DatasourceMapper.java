package com.isxcode.star.modules.datasource.mapper;

import com.isxcode.star.api.datasource.dto.ConnectInfo;
import com.isxcode.star.api.datasource.req.AddDatasourceReq;
import com.isxcode.star.api.datasource.req.CheckConnectReq;
import com.isxcode.star.api.datasource.req.UpdateDatasourceReq;
import com.isxcode.star.api.datasource.res.GetDefaultDatabaseDriverRes;
import com.isxcode.star.api.datasource.res.PageDatabaseDriverRes;
import com.isxcode.star.api.datasource.res.PageDatasourceRes;
import com.isxcode.star.modules.datasource.entity.DatabaseDriverEntity;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DatasourceMapper {

    @Mapping(target = "kafkaConfig", ignore = true)
    DatasourceEntity dasAddDatasourceReqToDatasourceEntity(AddDatasourceReq dasAddDatasourceReq);

    @Mapping(target = "kafkaConfig", ignore = true)
    DatasourceEntity checkConnectReqToDatasourceEntity(CheckConnectReq checkConnectReq);

    @Mapping(source = "dasUpdateDatasourceReq.passwd", target = "passwd")
    @Mapping(source = "dasUpdateDatasourceReq.remark", target = "remark")
    @Mapping(source = "dasUpdateDatasourceReq.dbType", target = "dbType")
    @Mapping(source = "dasUpdateDatasourceReq.jdbcUrl", target = "jdbcUrl")
    @Mapping(source = "dasUpdateDatasourceReq.username", target = "username")
    @Mapping(source = "dasUpdateDatasourceReq.metastoreUris", target = "metastoreUris")
    @Mapping(source = "dasUpdateDatasourceReq.driverId", target = "driverId")
    @Mapping(source = "dasUpdateDatasourceReq.name", target = "name")
    @Mapping(target = "id", source = "datasourceEntity.id")
    @Mapping(target = "kafkaConfig", ignore = true)
    DatasourceEntity dasUpdateDatasourceReqToDatasourceEntity(UpdateDatasourceReq dasUpdateDatasourceReq,
        DatasourceEntity datasourceEntity);

    @Mapping(target = "checkDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "kafkaConfigStr", source = "kafkaConfig")
    @Mapping(target = "kafkaConfig", ignore = true)
    PageDatasourceRes datasourceEntityToQueryDatasourceRes(DatasourceEntity datasourceEntity);

    @Mapping(target = "createDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    PageDatabaseDriverRes dataDriverEntityToPageDatabaseDriverRes(DatabaseDriverEntity databaseDriverEntity);

    GetDefaultDatabaseDriverRes databaseDriverEntityToGetDefaultDatabaseDriverRes(
        DatabaseDriverEntity databaseDriverEntity);

    ConnectInfo datasourceEntityToConnectInfo(DatasourceEntity datasourceEntity);
}
