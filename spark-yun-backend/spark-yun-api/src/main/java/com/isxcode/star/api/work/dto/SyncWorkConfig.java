package com.isxcode.star.api.work.dto;

import com.isxcode.star.api.datasource.dto.KafkaConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class SyncWorkConfig {

    @Schema(title = "作业唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
    private String workId;

    @Schema(title = "来源数据库类型", example = "mysql")
    private String sourceDBType;

    @Schema(title = "来源数据库唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
    private String sourceDBId;

    @Schema(title = "来源数据库表名", example = "part_table")
    private String sourceTable;

    @Schema(title = "来源数据库表的分区键", example = "part_column")
    private String partitionColumn;

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

    @Schema(title = "kafka解析方式", example = "JSON/CSV")
    private String kafkaDataType;

    @Schema(title = "csv分隔符", example = ";")
    private String csvSplitBy;

    @Schema(title = "json模版", example = "{}")
    private String jsonTemplate;

    @Schema(title = "json解析方式", example = "LIST/OBJECT")
    private String jsonDataType;

    @Schema(title = "根目录jsonPath", example = "$..")
    private String rootJsonPath;

    private KafkaConfig kafkaConfig;

    private DatasourceConfig sourceDatabase;

    private DatasourceConfig targetDatabase;

    @Schema(title = "实时作业监听的类型， u/d/c")
    private List<String> cat;

    private String kafkaSourceId;
}
