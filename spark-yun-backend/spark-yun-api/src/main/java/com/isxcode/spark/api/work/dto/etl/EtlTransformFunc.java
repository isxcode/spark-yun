package com.isxcode.spark.api.work.dto.etl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EtlTransformFunc {

    private String funcCode;

    private String funcName;

    private Integer inputSize;

    private String funcFormat;
}

