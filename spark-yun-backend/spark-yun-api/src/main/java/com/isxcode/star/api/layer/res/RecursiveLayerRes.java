package com.isxcode.star.api.layer.res;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.star.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecursiveLayerRes {

    private String id;

    private String name;

    private String tableRule;

    private String parentLayerId;

    private String parentIdList;

    private String parentNameList;

    private String remark;

    private String createBy;

    private String createUsername;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDateTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastModifiedDateTime;

    private List<RecursiveLayerRes> children;
}
