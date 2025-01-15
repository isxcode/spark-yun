package com.isxcode.star.api.container.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class UpdateContainerReq {

    @Schema(title = "containerId", example = "sy_213")
    @NotEmpty(message = "id不能为空")
    private String id;

    @Schema(title = "容器名称", example = "测试容器")
    @NotEmpty(message = "name不能为空")
    private String name;

    @Schema(title = "集群id", example = "sy_973933ae11d64eddb3859021a91a520f")
    @NotEmpty(message = "clusterId不能为空")
    private String clusterId;

    @Schema(title = "数据源id", example = "sy_973933ae11d64eddb3859021a91a520f")
    @NotEmpty(message = "datasourceId不能为空")
    private String datasourceId;

    @Schema(title = "spark配置", example = "{}")
    private String sparkConfig;

    @Schema(title = "资源等级", example = "LOW/CUSTOM/HIGH/MEDIUM")
    @Pattern(regexp = "^(LOW|CUSTOM|HIGH|MEDIUM)$", message = "resourceLevel类型不支持")
    @NotEmpty(message = "resourceLevel不能为空")
    private String resourceLevel;

    @Schema(title = "备注", example = "备注123")
    private String remark;
}
