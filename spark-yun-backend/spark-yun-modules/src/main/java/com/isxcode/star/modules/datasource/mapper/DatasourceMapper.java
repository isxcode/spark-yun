package com.isxcode.star.modules.datasource.mapper;

import com.isxcode.star.api.datasource.pojos.req.AddDatasourceReq;
import com.isxcode.star.api.datasource.pojos.req.UpdateDatasourceReq;
import com.isxcode.star.api.datasource.pojos.res.GetDefaultDatabaseDriverRes;
import com.isxcode.star.api.datasource.pojos.res.PageDatabaseDriverRes;
import com.isxcode.star.api.datasource.pojos.res.PageDatasourceRes;
import com.isxcode.star.modules.datasource.entity.DatabaseDriverEntity;
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
	DatasourceEntity dasUpdateDatasourceReqToDatasourceEntity(UpdateDatasourceReq dasUpdateDatasourceReq,
			DatasourceEntity datasourceEntity);

	@Mapping(target = "checkDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
	PageDatasourceRes datasourceEntityToQueryDatasourceRes(DatasourceEntity datasourceEntity);

	List<PageDatasourceRes> datasourceEntityToQueryDatasourceResList(List<DatasourceEntity> datasourceEntity);

	default Page<PageDatasourceRes> datasourceEntityToQueryDatasourceResPage(Page<DatasourceEntity> pageDatasource) {
		List<PageDatasourceRes> dtoList = datasourceEntityToQueryDatasourceResList(pageDatasource.getContent());
		return new PageImpl<>(dtoList, pageDatasource.getPageable(), pageDatasource.getTotalElements());
	}

	@Mapping(target = "createDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
	PageDatabaseDriverRes dataDriverEntityToPageDatabaseDriverRes(DatabaseDriverEntity databaseDriverEntity);

	List<PageDatabaseDriverRes> dataDriverEntityToPageDatabaseDriverResList(
			List<DatabaseDriverEntity> datasourceEntity);

	default Page<PageDatabaseDriverRes> dataDriverEntityToPageDatabaseDriverResPage(
			Page<DatabaseDriverEntity> pageDatasource) {
		List<PageDatabaseDriverRes> dtoList = dataDriverEntityToPageDatabaseDriverResList(pageDatasource.getContent());
		return new PageImpl<>(dtoList, pageDatasource.getPageable(), pageDatasource.getTotalElements());
	}

	GetDefaultDatabaseDriverRes databaseDriverEntityToGetDefaultDatabaseDriverRes(
			DatabaseDriverEntity databaseDriverEntity);
}
