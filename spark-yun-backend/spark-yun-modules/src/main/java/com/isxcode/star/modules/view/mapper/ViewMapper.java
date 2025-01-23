package com.isxcode.star.modules.view.mapper;

import com.isxcode.star.api.view.dto.CardInfo;
import com.isxcode.star.api.view.req.AddViewCardReq;
import com.isxcode.star.api.view.req.AddViewReq;
import com.isxcode.star.api.view.res.AddViewRes;
import com.isxcode.star.api.view.res.GetViewLinkInfoRes;
import com.isxcode.star.api.view.res.PageViewCardRes;
import com.isxcode.star.api.view.res.PageViewRes;
import com.isxcode.star.modules.view.entity.ViewCardEntity;
import com.isxcode.star.modules.view.entity.ViewEntity;
import com.isxcode.star.modules.view.entity.ViewLinkEntity;
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

    GetViewLinkInfoRes viewLinkEntityToGetFormLinkInfoRes(ViewLinkEntity viewLinkEntity);
}
