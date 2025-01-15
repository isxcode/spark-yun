package com.isxcode.star.api.view.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AddViewCardReq {

    @Schema(title = "大屏组件名称", example = "饼图测试")
    @NotEmpty(message = "name不能为空")
    private String name;

    @Schema(title = "大屏组件名称", example = "Line/Bar/Pie ")
    @NotEmpty(message = "type不能为空")
    private String type;

    @Schema(title = "数据源id", example = "sy_a05cfc7bda0b41c196ada563052f68fe")
    @NotEmpty(message = "datasourceId不能为空")
    private String datasourceId;

    @Schema(title = "备注", example = "备注123")
    private String remark;
}
