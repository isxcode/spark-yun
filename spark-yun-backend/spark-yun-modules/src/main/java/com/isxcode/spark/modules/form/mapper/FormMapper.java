package com.isxcode.spark.modules.form.mapper;

import com.isxcode.spark.api.form.req.AddFormReq;
import com.isxcode.spark.api.form.res.AddFormRes;
import com.isxcode.spark.api.form.res.FormPageRes;
import com.isxcode.spark.api.form.res.GetFormLinkInfoRes;
import com.isxcode.spark.modules.form.entity.FormEntity;
import com.isxcode.spark.modules.form.entity.FormLinkEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FormMapper {

    FormEntity addFormReqToFormEntity(AddFormReq addFormReq);

    AddFormRes formEntityToAddFormRes(FormEntity formEntity);

    FormPageRes formEntityToFormPageRes(FormEntity formEntity);

    GetFormLinkInfoRes formLinkEntityToGetFormLinkInfoRes(FormLinkEntity formLinkEntity);
}
