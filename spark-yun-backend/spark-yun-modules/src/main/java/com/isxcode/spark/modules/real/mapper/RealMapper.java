package com.isxcode.spark.modules.real.mapper;

import com.isxcode.spark.api.real.req.AddRealReq;
import com.isxcode.spark.api.real.res.GetRealSubmitLogRes;
import com.isxcode.spark.api.real.res.PageRealRes;
import com.isxcode.spark.modules.real.entity.RealEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RealMapper {

    RealEntity addRealReqToRealEntity(AddRealReq addRealReq);

    PageRealRes realEntityToPageRealRes(RealEntity real);

    GetRealSubmitLogRes realEntityToGetRealSubmitLogRes(RealEntity real);
}
