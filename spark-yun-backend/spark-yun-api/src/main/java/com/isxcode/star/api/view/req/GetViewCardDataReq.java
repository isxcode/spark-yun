package com.isxcode.star.api.view.req;

import com.isxcode.star.api.view.dto.DataSql;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetViewCardDataReq {

    @Schema(title = "大屏id", example = "sy_123")
    @NotEmpty(message = "id不能为空")
    private String id;

    @Schema(title = "聚合sql", example = "")
    @NotNull(message = "dataSql不能为空")
    private DataSql dataSql;
}
