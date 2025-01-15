package com.isxcode.star.modules.form.mapper;

import com.isxcode.star.api.form.req.AddFormReq;
import com.isxcode.star.api.form.res.AddFormRes;
import com.isxcode.star.api.form.res.FormPageRes;
import com.isxcode.star.api.form.res.GetFormLinkInfoRes;
import com.isxcode.star.modules.form.entity.FormEntity;
import com.isxcode.star.modules.form.entity.FormLinkEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FormMapper {

    FormEntity addFormReqToFormEntity(AddFormReq addFormReq);

    AddFormRes formEntityToAddFormRes(FormEntity formEntity);

    FormPageRes formEntityToFormPageRes(FormEntity formEntity);

    GetFormLinkInfoRes formLinkEntityToGetFormLinkInfoRes(FormLinkEntity formLinkEntity);
}
