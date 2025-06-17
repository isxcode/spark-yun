package com.isxcode.star.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateDataModelReq {

    @Schema(title = "数据模型id", example = "123")
    @NotEmpty(message = "id不能为空")
    private String id;

    @Schema(title = "数据模型名称", example = "123")
    @NotEmpty(message = "name不能为空")
    private String name;

    @Schema(title = "数据分层id", example = "123")
    @NotEmpty(message = "layerId不能为空")
    private String layerId;

    @Schema(title = "数据类型", example = "123")
    @NotEmpty(message = "dbType不能为空")
    private String dbType;

    @Schema(title = "数据源id", example = "123")
    @NotEmpty(message = "datasourceId不能为空")
    private String datasourceId;

    @Schema(title = "表名", example = "123")
    @NotEmpty(message = "tableName不能为空")
    private String tableName;

    @Schema(title = "表高级配置", example = "123")
    private String tableConfig;

    @Schema(title = "备注", example = "123")
    private String remark;
}
