package com.isxcode.spark.modules.model.mapper;

import com.isxcode.spark.api.model.req.AddColumnFormatReq;
import com.isxcode.spark.api.model.req.UpdateColumnFormatReq;
import com.isxcode.spark.api.model.res.AddColumnFormatRes;
import com.isxcode.spark.api.model.res.ColumnFormatPageRes;
import com.isxcode.spark.modules.model.entity.ColumnFormatEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ColumnFormatMapper {

    ColumnFormatEntity addColumnFormatReqToColumnFormatEntity(AddColumnFormatReq addColumnFormatReq);

    @Mapping(target = "name", source = "updateColumnFormatReq.name")
    @Mapping(target = "columnTypeCode", source = "updateColumnFormatReq.columnTypeCode")
    @Mapping(target = "columnType", source = "updateColumnFormatReq.columnType")
    @Mapping(target = "columnRule", source = "updateColumnFormatReq.columnRule")
    @Mapping(target = "remark", source = "updateColumnFormatReq.remark")
    @Mapping(target = "defaultValue", source = "updateColumnFormatReq.defaultValue")
    @Mapping(target = "isNull", source = "updateColumnFormatReq.isNull")
    @Mapping(target = "isDuplicate", source = "updateColumnFormatReq.isDuplicate")
    @Mapping(target = "isPartition", source = "updateColumnFormatReq.isPartition")
    @Mapping(target = "isPrimary", source = "updateColumnFormatReq.isPrimary")
    @Mapping(target = "id", source = "columnFormatEntity.id")
    ColumnFormatEntity updateColumnFormatReqToColumnFormatEntity(UpdateColumnFormatReq updateColumnFormatReq,
        ColumnFormatEntity columnFormatEntity);

    ColumnFormatPageRes columnFormatEntityToColumnFormatPageRes(ColumnFormatEntity columnFormatEntity);

    AddColumnFormatRes columnFormatEntityToAddColumnFormatRes(ColumnFormatEntity columnFormatEntity);
}
