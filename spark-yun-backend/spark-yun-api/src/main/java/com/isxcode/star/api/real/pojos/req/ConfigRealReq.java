package com.isxcode.star.api.real.pojos.req;

import com.isxcode.star.api.work.pojos.dto.SyncWorkConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

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
    private List<String> LibList;

    @Schema(title = "spark配置")
    private Map<String, String> sparkConfig;

    @Schema(title = "集群id")
    private String clusterId;
}
