package com.isxcode.star.api.form.res;

import lombok.Data;

@Data
public class AddFormRes {

    private String id;

    private String name;

    private String datasourceId;

    private String mainTable;

    private String remark;

    private String formVersion;
}
