package com.isxcode.star.api.work.pojos.res;

import com.isxcode.star.api.datasource.pojos.dto.ColumnMetaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetDataSourceColumnsRes {

	private List<ColumnMetaDto> columns;
}
