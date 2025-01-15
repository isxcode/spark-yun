package com.isxcode.star.modules.meta.mapper;

import com.isxcode.star.api.meta.ao.MetaColumnAo;
import com.isxcode.star.api.meta.ao.MetaTableAo;
import com.isxcode.star.api.datasource.dto.QueryColumnDto;
import com.isxcode.star.api.datasource.dto.QueryTableDto;
import com.isxcode.star.api.meta.req.AddMetaWokReq;
import com.isxcode.star.api.meta.res.*;
import com.isxcode.star.modules.meta.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface MetaMapper {

    PageMetaDatabaseRes metaDatabaseEntityToPageMetaDatabaseRes(MetaDatabaseEntity metaDatabaseEntity);

    PageMetaTableRes metaTableEntityToPageMetaTableRes(MetaTableAo metaTableAo);

    @Mapping(target = "cronConfig", ignore = true)
    MetaWorkEntity addMetaWorkToMetaWorkEntity(AddMetaWokReq addMetaWokReq);

    @Mapping(target = "cronConfigStr", source = "cronConfig")
    @Mapping(target = "cronConfig", ignore = true)
    PageMetaWorkRes metaWorkEntityToPageMetaWorkRes(MetaWorkEntity metaWorkEntity);

    MetaTableEntity queryTableDtoToMetaTableEntity(QueryTableDto queryTableDto);

    List<MetaTableEntity> queryTableDtoListToMetaTableEntityList(List<QueryTableDto> queryTableDtos);

    MetaColumnEntity queryColumnDtoToMetaColumnEntity(QueryColumnDto queryColumnDto);

    List<MetaColumnEntity> queryColumnDtoListToMetaColumnEntityList(List<QueryColumnDto> queryColumnDtos);

    PageMetaColumnRes metaColumnEntityToPageMetaColumnRes(MetaColumnAo metaColumnAo);

    PageMetaWorkInstanceRes metaInstanceEntityToPageMetaWorkInstanceRes(MetaInstanceEntity metaInstanceEntity);

    @Mapping(target = "collectDateTime", source = "lastModifiedDateTime")
    GetMetaTableInfoRes metaTableEntityToGetMetaTableInfoRes(MetaTableEntity metaTableEntity);

    @Mapping(target = "datasourceId", source = "metaTableInfoEntity.datasourceId")
    @Mapping(target = "tableName", source = "metaTableInfoEntity.tableName")
    @Mapping(target = "tableComment", source = "metaTableEntity.tableComment")
    @Mapping(target = "refreshDateTime", source = "metaTableInfoEntity.lastModifiedDateTime")
    @Mapping(target = "collectDateTime", source = "metaTableEntity.lastModifiedDateTime")
    GetMetaTableInfoRes metaTableInfoEntityAndmetaTableEntityToGetMetaTableInfoRes(
        MetaTableInfoEntity metaTableInfoEntity, MetaTableEntity metaTableEntity);

    QueryColumnDto metaColumnEntityToQueryColumnDto(MetaColumnEntity metaColumnEntity);

    List<QueryColumnDto> metaColumnEntitiesToQueryColumnDtoList(List<MetaColumnAo> metaColumnEntities);
}
