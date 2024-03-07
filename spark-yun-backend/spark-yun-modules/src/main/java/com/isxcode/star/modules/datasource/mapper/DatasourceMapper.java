package com.isxcode.star.modules.datasource.mapper;

import com.isxcode.star.api.datasource.pojos.req.AddDatasourceReq;
import com.isxcode.star.api.datasource.pojos.req.UpdateDatasourceReq;
import com.isxcode.star.api.datasource.pojos.res.GetDefaultDatabaseDriverRes;
import com.isxcode.star.api.datasource.pojos.res.PageDatabaseDriverRes;
import com.isxcode.star.api.datasource.pojos.res.PageDatasourceRes;
import com.isxcode.star.modules.datasource.entity.DatabaseDriverEntity;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** mapstruct映射. */
@Mapper(componentModel = "spring")
public interface DatasourceMapper {

	/** dasAddDatasourceReq转DatasourceEntity. */
  @Mapping(target = "kafkaConfig",ignore = true)
	DatasourceEntity dasAddDatasourceReqToDatasourceEntity(AddDatasourceReq dasAddDatasourceReq);

	@Mapping(source = "dasUpdateDatasourceReq.passwd", target = "passwd")
	@Mapping(source = "dasUpdateDatasourceReq.remark", target = "remark")
	@Mapping(source = "dasUpdateDatasourceReq.dbType", target = "dbType")
	@Mapping(source = "dasUpdateDatasourceReq.jdbcUrl", target = "jdbcUrl")
	@Mapping(source = "dasUpdateDatasourceReq.username", target = "username")
	@Mapping(source = "dasUpdateDatasourceReq.metastoreUris", target = "metastoreUris")
	@Mapping(source = "dasUpdateDatasourceReq.driverId", target = "driverId")
	@Mapping(source = "dasUpdateDatasourceReq.name", target = "name")
	@Mapping(target = "id", source = "datasourceEntity.id")
  @Mapping(target = "kafkaConfig",ignore = true)
	DatasourceEntity dasUpdateDatasourceReqToDatasourceEntity(UpdateDatasourceReq dasUpdateDatasourceReq,
			DatasourceEntity datasourceEntity);

	@Mapping(target = "checkDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
	PageDatasourceRes datasourceEntityToQueryDatasourceRes(DatasourceEntity datasourceEntity);

	@Mapping(target = "createDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
	PageDatabaseDriverRes dataDriverEntityToPageDatabaseDriverRes(DatabaseDriverEntity databaseDriverEntity);

	GetDefaultDatabaseDriverRes databaseDriverEntityToGetDefaultDatabaseDriverRes(
			DatabaseDriverEntity databaseDriverEntity);
}
