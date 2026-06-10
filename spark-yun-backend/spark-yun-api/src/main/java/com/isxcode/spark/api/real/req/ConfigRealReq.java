package com.isxcode.spark.api.real.req;

import com.isxcode.spark.api.work.dto.SyncWorkConfig;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class ConfigRealReq {

    @Schema(title = "实时同步作业id", example = "sy_123")
    @NotEmpty(message = "id不能为空")
    private String realId;

    @Schema(title = "数据同步规则")
    private SyncWorkConfig syncConfig;

    @Schema(title = "自定义函数选择")
    private List<String> funcList;

    @Schema(title = "依赖选择")
    @JsonProperty("LibList")
    private List<String> libList;

    @Schema(title = "spark配置字符串")
    private String sparkConfigJson;

    @Schema(title = "集群id")
    private String clusterId;
}
