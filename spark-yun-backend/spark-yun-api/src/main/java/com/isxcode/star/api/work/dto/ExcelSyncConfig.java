package com.isxcode.star.api.work.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ExcelSyncConfig {

    @Schema(title = "作业唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
    private String workId;

    @Schema(title = "来源excel唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
    private String sourceFileId;

    @Schema(title = "是否存在表头", example = "true")
    private boolean hasHeader;

    @Schema(title = "是否开启文件替换", example = "false")
    private boolean fileReplace;

    @Schema(title = "文件名规则", example = "某某文件")
    private String filePattern;

    @Schema(title = "来源数据库查询条件", example = "WHERE id = 1")
    private String queryCondition;

    @Schema(title = "目标数据库类型", example = "mysql")
    private String targetDBType;

    @Schema(title = "目标数据库唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
    private String targetDBId;

    @Schema(title = "目标数据库表名", example = "part")
    private String targetTable;

    @Schema(title = "写入模式", example = "OVERWRITE or INTO")
    private String overMode;

    @Schema(title = "来源表信息", example = "[{\"code\":\"installed_rank\",\"type\":\"String\",\"sql\":\"\"}]")
    private List<SyncColumnInfo> sourceTableColumn;

    @Schema(title = "去向表信息", example = "[{\"code\":\"installed_rank\",\"type\":\"String\"}]")
    private List<SyncColumnInfo> targetTableColumn;

    @Schema(title = "字段映射关系", example = "[{\"source\": \"installed_rank\",\"target\": \"installed_rank\"}]")
    private List<SyncColumnMap> columnMap;

    private DatasourceConfig targetDatabase;
}
