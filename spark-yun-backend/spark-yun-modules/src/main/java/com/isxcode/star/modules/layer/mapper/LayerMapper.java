package com.isxcode.star.modules.layer.mapper;

import com.isxcode.star.api.layer.req.AddLayerReq;
import com.isxcode.star.api.layer.res.LayerPageRes;
import com.isxcode.star.api.layer.res.RecursiveLayerRes;
import com.isxcode.star.modules.layer.entity.LayerEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LayerMapper {

    LayerEntity addLayerReqToLayerEntity(AddLayerReq addLayerReq);

    LayerPageRes layerEntityToLayerPageRes(LayerEntity layerEntity);

    RecursiveLayerRes layerEntityToRecursiveLayerRes(LayerEntity layerEntity);

    List<RecursiveLayerRes> layerEntityListToRecursiveLayerResList(List<LayerEntity> layerEntity);
}
