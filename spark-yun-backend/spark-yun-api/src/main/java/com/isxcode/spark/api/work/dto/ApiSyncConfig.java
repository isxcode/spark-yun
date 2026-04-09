package com.isxcode.spark.api.work.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ApiSyncConfig {

    @Schema(title = "作业唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
    private String workId;

    @Schema(title = "来源类型", example = "API/DATASOURCE")
    private String sourceType;

    @Schema(title = "去向类型", example = "API/DATASOURCE")
    private String targetType;

    @Schema(title = "来源数据库唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
    private String sourceDBId;

    @Schema(title = "来源数据库表名", example = "part_table")
    private String sourceTable;

    @Schema(title = "来源数据库表的分区键", example = "part_column")
    private String partitionColumn;

    @Schema(title = "来源数据库查询条件", example = "WHERE id = 1")
    private String queryCondition;

    @Schema(title = "请求类型", example = "GET/POST")
    private String sourceRequestType;

    @Schema(title = "写入模式", example = "OVERWRITE or INTO")
    private String overMode;

    @Schema(title = "请求地址", example = "http://")
    private String sourceRequestHttp;

    @Schema(title = "请求头", example = "")
    private Map<String, String> sourceRequestHeader;

    @Schema(title = "请求模版", example = "")
    private String sourceRequestBody;

    @Schema(title = "响应模版", example = "")
    private String sourceResponseBody;

    @Schema(title = "每页大小", example = "")
    private Integer sourcePageSize;

    @Schema(title = "开始页", example = "")
    private Integer sourceStartPage;

    @Schema(title = "结束页", example = "")
    private Integer sourceEndPage;

    @Schema(title = "同步全部", example = "")
    private Boolean syncAll;

    @Schema(title = "目标数据库唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
    private String targetDBId;

    @Schema(title = "目标数据库表名", example = "part")
    private String targetTable;

    @Schema(title = "目标类型", example = "GET/POST")
    private String targetRequestType;

    @Schema(title = "目标地址", example = "http://")
    private String targetRequestHttp;

    @Schema(title = "目标请求头", example = "")
    private Map<String, String> targetRequestHeader;

    @Schema(title = "目标模版", example = "")
    private String targetRequestBody;

    @Schema(title = "来源表信息", example = "[{\"code\":\"installed_rank\",\"type\":\"String\",\"sql\":\"\"}]")
    private List<SyncColumnInfo> sourceTableColumn;

    @Schema(title = "去向表信息", example = "[{\"code\":\"installed_rank\",\"type\":\"String\"}]")
    private List<SyncColumnInfo> targetTableColumn;

    @Schema(title = "字段映射关系", example = "[{\"source\": \"installed_rank\",\"target\": \"installed_rank\"}]")
    private List<SyncColumnMap> columnMap;

    @Schema(title = "解析类型", example = "LIST/OBJECT")
    private String jsonDataType;

    @Schema(title = "节点根目录jsonPath", example = "$..")
    private String nodeRootJsonPath;

    private DatasourceConfig targetDatabase;
}
