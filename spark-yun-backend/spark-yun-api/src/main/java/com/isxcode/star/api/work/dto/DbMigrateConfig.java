package com.isxcode.star.api.work.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DbMigrateConfig {

    private String includeTargetRule;

    @Schema(title = "作业唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
    private String workId;

    @Schema(title = "来源数据库类型", example = "mysql")
    private String sourceDBType;

    @Schema(title = "来源数据库唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
    private String sourceDBId;

    @Schema(title = "目标数据库类型", example = "mysql")
    private String targetDBType;

    @Schema(title = "目标数据库唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
    private String targetDBId;

    private DatasourceConfig sourceDatabase;

    private DatasourceConfig targetDatabase;

    private List<String> syncTables;
}
