package com.isxcode.spark.api.work.res;

import com.isxcode.spark.api.datasource.dto.ColumnMetaDto;
import lombok.Data;

import java.util.List;

@Data
public class ParseEtlCustomDataSqlRes {

    private List<ColumnMetaDto> columns;
}
