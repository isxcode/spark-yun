package com.isxcode.star.modules.real.mapper;

import com.isxcode.star.api.real.req.AddRealReq;
import com.isxcode.star.api.real.res.GetRealSubmitLogRes;
import com.isxcode.star.api.real.res.PageRealRes;
import com.isxcode.star.modules.real.entity.RealEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RealMapper {

    RealEntity addRealReqToRealEntity(AddRealReq addRealReq);

    PageRealRes realEntityToPageRealRes(RealEntity real);

    GetRealSubmitLogRes realEntityToGetRealSubmitLogRes(RealEntity real);
}
