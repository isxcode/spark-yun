package com.isxcode.star.modules.model.mapper;

import com.isxcode.star.api.model.ao.DataModelColumnAo;
import com.isxcode.star.api.model.req.*;
import com.isxcode.star.api.model.res.DataModelColumnPageRes;
import com.isxcode.star.api.model.res.DataModelPageRes;
import com.isxcode.star.modules.model.entity.DataModelColumnEntity;
import com.isxcode.star.modules.model.entity.DataModelEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DataModelMapper {

    DataModelEntity addDataModelReqToDataModelEntity(AddDataModelReq addDataModelReq);

    @Mapping(target = "name", source = "updateDataModelReq.name")
    @Mapping(target = "layerId", source = "updateDataModelReq.layerId")
    @Mapping(target = "dbType", source = "updateDataModelReq.dbType")
    @Mapping(target = "datasourceId", source = "updateDataModelReq.datasourceId")
    @Mapping(target = "tableName", source = "updateDataModelReq.tableName")
    @Mapping(target = "modelType", source = "updateDataModelReq.modelType")
    @Mapping(target = "tableConfig", source = "updateDataModelReq.tableConfig")
    @Mapping(target = "remark", source = "updateDataModelReq.remark")
    @Mapping(target = "id", source = "dataModelEntity.id")
    DataModelEntity updateDataModelReqToDataModelEntity(UpdateDataModelReq updateDataModelReq,
                                                        DataModelEntity dataModelEntity);

    DataModelPageRes dataModelEntityToDataModelPageRes(DataModelEntity dataModelEntity);

    DataModelColumnEntity addDataModelColumnReqToDataModelColumnEntity(AddDataModelColumnReq addDataModelColumnReq);

    @Mapping(target = "name", source = "updateDataModelColumnReq.name")
    @Mapping(target = "columnName", source = "updateDataModelColumnReq.columnName")
    @Mapping(target = "columnFormatId", source = "updateDataModelColumnReq.columnFormatId")
    @Mapping(target = "remark", source = "updateDataModelColumnReq.remark")
    @Mapping(target = "id", source = "dataModelColumnEntity.id")
    DataModelColumnEntity updateDataModelColumnReqToDataModelColumnEntity(
        UpdateDataModelColumnReq updateDataModelColumnReq, DataModelColumnEntity dataModelColumnEntity);

    DataModelColumnPageRes dataModelColumnAoToDataModelColumnPageRes(DataModelColumnAo dataModelColumnAo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "versionNumber", ignore = true)
    @Mapping(target = "remark", source = "copyDataModelReq.remark")
    @Mapping(target = "name", source = "copyDataModelReq.name")
    @Mapping(target = "tableName", source = "copyDataModelReq.tableName")
    @Mapping(target = "layerId", source = "copyDataModelReq.layerId")
    @Mapping(target = "dbType", source = "dataModelEntity.dbType")
    @Mapping(target = "datasourceId", source = "dataModelEntity.datasourceId")
    @Mapping(target = "modelType", source = "dataModelEntity.modelType")
    @Mapping(target = "tableConfig", source = "dataModelEntity.tableConfig")
    DataModelEntity copyDataModelReqToDataModelEntity(CopyDataModelReq copyDataModelReq, DataModelEntity dataModelEntity);
}
