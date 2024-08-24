package com.isxcode.star.api.meta.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExportTableExcelReq {

    @Schema(title = "数据源id", example = "sy_e4d80a6b561d47afa81504e93054e8e8")
    @NotEmpty(message = "datasourceId不能为空")
    private String datasourceId;

    @Schema(title = "表名", example = "xxx")
    @NotEmpty(message = "tableName不能为空")
    private String tableName;
}
