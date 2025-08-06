package com.isxcode.spark.modules.layer.mapper;

import com.isxcode.spark.api.layer.req.AddLayerReq;
import com.isxcode.spark.api.layer.res.LayerPageRes;
import com.isxcode.spark.api.layer.res.RecursiveLayerRes;
import com.isxcode.spark.modules.layer.entity.LayerEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LayerMapper {

    LayerEntity addLayerReqToLayerEntity(AddLayerReq addLayerReq);

    LayerPageRes layerEntityToLayerPageRes(LayerEntity layerEntity);

    RecursiveLayerRes layerEntityToRecursiveLayerRes(LayerEntity layerEntity);

    List<RecursiveLayerRes> layerEntityListToRecursiveLayerResList(List<LayerEntity> layerEntity);
}
