package com.isxcode.spark.api.meta.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetColumnLinageRes {

    private String lineageId;

    private String dbId;

    private String dbName;

    private String dbType;

    private String dbRemark;

    private String tableName;

    private String columnName;

    private String workId;

    private String workVersionId;

    private String workName;

    private String workType;

    private String remark;

    private List<GetColumnLinageRes> children;

    private List<GetColumnLinageRes> parent;
}
