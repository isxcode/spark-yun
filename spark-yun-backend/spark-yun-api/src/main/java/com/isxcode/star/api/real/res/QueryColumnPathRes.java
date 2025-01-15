package com.isxcode.star.api.real.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryColumnPathRes {

    @Schema(title = "字段名", example = "username")
    private String name;

    @Schema(title = "类型", example = "int")
    private String type;

    @Schema(title = "jsonPath", example = "$.users")
    private String jsonPath;
}
