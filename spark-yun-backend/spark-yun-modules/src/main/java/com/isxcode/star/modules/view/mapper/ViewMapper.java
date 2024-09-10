package com.isxcode.star.modules.view.mapper;

import com.isxcode.star.api.view.pojo.dto.CardInfo;
import com.isxcode.star.api.view.pojo.req.AddViewCardReq;
import com.isxcode.star.api.view.pojo.req.AddViewReq;
import com.isxcode.star.api.view.pojo.res.AddViewRes;
import com.isxcode.star.api.view.pojo.res.PageViewCardRes;
import com.isxcode.star.api.view.pojo.res.PageViewRes;
import com.isxcode.star.modules.view.entity.ViewCardEntity;
import com.isxcode.star.modules.view.entity.ViewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ViewMapper {

    ViewCardEntity addViewCardReqToViewCardEntity(AddViewCardReq addViewCardReq);

    PageViewCardRes viewCardEntityToPageViewCardRes(ViewCardEntity viewCardEntity);

    PageViewRes viewEntityToPageViewRes(ViewEntity viewEntity);

    @Mapping(ignore = true, target = "dataSql")
    @Mapping(ignore = true, target = "exampleData")
    CardInfo viewCardEntityToCardInfo(ViewCardEntity viewCardEntity);

    ViewEntity addViewReqToViewEntity(AddViewReq addViewReq);

    AddViewRes viewEntityToAddViewRes(ViewEntity viewEntity);
}
