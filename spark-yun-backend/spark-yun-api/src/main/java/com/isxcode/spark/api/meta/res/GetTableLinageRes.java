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
public class GetTableLinageRes {

    private String dbId;

    private String dbName;

    private String dbType;

    private String tableName;

    private List<GetTableLinageRes> sonTableLineageList;

    private List<GetTableLinageRes> parentTableLineageList;
}
