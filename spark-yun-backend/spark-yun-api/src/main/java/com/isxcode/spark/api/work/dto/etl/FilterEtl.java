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
public class FilterEtl {

    private List<FilterEtl> groupFilter;

    private String filterAndOr;

    private String filterType;

    private String filterColumn;

    private String filterCondition;

    private String filterValue;

    private String customFilter;
}

