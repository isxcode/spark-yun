package com.isxcode.star.api.view.pojo.res;

import com.isxcode.star.api.view.pojo.dto.EchartOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetViewCardDataByIdRes {

	private EchartOption viewData;
}
