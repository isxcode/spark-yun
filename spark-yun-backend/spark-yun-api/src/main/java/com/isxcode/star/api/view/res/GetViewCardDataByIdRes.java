package com.isxcode.star.api.view.res;

import com.isxcode.star.api.view.dto.EchartOption;
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
