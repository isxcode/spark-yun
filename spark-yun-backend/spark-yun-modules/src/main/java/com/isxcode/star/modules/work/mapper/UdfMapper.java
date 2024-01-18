package com.isxcode.star.modules.work.mapper;

import com.isxcode.star.api.work.pojos.req.AddUdfReq;
import com.isxcode.star.api.work.pojos.req.UpdateUdfReq;
import com.isxcode.star.api.work.pojos.res.PageUdfRes;
import com.isxcode.star.modules.work.entity.UdfEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** mapstruct映射. */
@Mapper(componentModel = "spring")
public interface UdfMapper {

	UdfEntity addUdfReqToUdfEntity(AddUdfReq addUdfReq);
	@Mapping(source = "updateUdfReq.type", target = "type")
	@Mapping(source = "updateUdfReq.funcName", target = "funcName")
	@Mapping(source = "updateUdfReq.className", target = "className")
	@Mapping(source = "updateUdfReq.resultType", target = "resultType")
	@Mapping(source = "updateUdfReq.status", target = "status")
	@Mapping(source = "udfEntity.id", target = "id")
	UdfEntity updateUdfReqToUdfEntity(UpdateUdfReq updateUdfReq, UdfEntity udfEntity);

	PageUdfRes udfEntityToPageUdfRes(UdfEntity udfEntity);
}
