package com.isxcode.spark.api.work.dto.etl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransformEtl {

    private String colName;

    private String transformWay;

    private String transformFunc;

    private List<String> inputValue;

    private String transformSql;
}

