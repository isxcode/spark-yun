package com.isxcode.star.api.model.req;

import lombok.Data;

import java.util.List;

@Data
public class UpdateDataModelColumnIndexReq {

    private String modelId;

    private List<String> dataModelColumnIdList;
}
