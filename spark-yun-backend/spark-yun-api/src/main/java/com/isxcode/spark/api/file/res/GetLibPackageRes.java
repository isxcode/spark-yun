package com.isxcode.spark.api.file.res;

import lombok.Data;

import java.util.List;

@Data
public class GetLibPackageRes {

    private String id;

    private String name;

    private List<String> fileIdList;

    private String remark;
}
