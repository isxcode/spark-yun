package com.isxcode.spark.modules.container.mapper;

import com.isxcode.spark.api.container.req.AddContainerReq;
import com.isxcode.spark.api.container.req.UpdateContainerReq;
import com.isxcode.spark.api.container.res.GetContainerRes;
import com.isxcode.spark.api.container.res.PageContainerRes;
import com.isxcode.spark.modules.container.entity.ContainerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContainerMapper {

    ContainerEntity addContainerToContainerEntity(AddContainerReq addContainerReq);

    @Mapping(target = "id", source = "containerEntity.id")
    @Mapping(target = "name", source = "updateContainerReq.name")
    @Mapping(target = "remark", source = "updateContainerReq.remark")
    @Mapping(target = "datasourceId", source = "updateContainerReq.datasourceId")
    @Mapping(target = "clusterId", source = "updateContainerReq.clusterId")
    @Mapping(target = "resourceLevel", source = "updateContainerReq.resourceLevel")
    @Mapping(ignore = true, target = "sparkConfig")
    ContainerEntity updateContainerToContainerEntity(UpdateContainerReq updateContainerReq,
        ContainerEntity containerEntity);

    PageContainerRes containerEntityToPageContainerRes(ContainerEntity containerEntity);

    GetContainerRes containerEntityToGetContainerRes(ContainerEntity containerEntity);
}
