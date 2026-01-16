package com.isxcode.spark.api.datasource.req;

import com.isxcode.spark.api.datasource.dto.ColumnMetaDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class GenerateCreateTableSqlReq {

    @Schema(description = "来源数据源类型", example = "")
    @NotEmpty(message = "fromDbType不能为空")
    private String fromDbType;

    @Schema(description = "来源数据表名", example = "")
    @NotEmpty(message = "fromTableName不能为空")
    private String fromTableName;

    @Schema(description = "来源数据表列表信息", example = "")
    private List<ColumnMetaDto> fromColumnList;

    @Schema(description = "去向数据源类型", example = "")
    @NotEmpty(message = "toDbType不能为空")
    private String toDbType;
}
