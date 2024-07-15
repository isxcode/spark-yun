package com.isxcode.star.api.datasource.pojos.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageDatabaseDriverRes {

    private String id;

    private String name;

    private String dbType;

    private String fileName;

    private String driverType;

    private String remark;

    private Boolean isDefaultDriver;

    private String createBy;

    private String createDateTime;
}
