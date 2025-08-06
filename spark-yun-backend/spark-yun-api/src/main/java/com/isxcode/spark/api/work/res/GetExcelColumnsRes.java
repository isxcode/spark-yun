package com.isxcode.spark.api.work.res;

import com.isxcode.spark.api.datasource.dto.ColumnMetaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetExcelColumnsRes {

    private List<ColumnMetaDto> columns;
}
