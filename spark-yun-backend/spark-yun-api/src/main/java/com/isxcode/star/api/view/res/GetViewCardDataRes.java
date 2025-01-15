package com.isxcode.star.api.view.res;

import com.isxcode.star.api.view.dto.EchartOption;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetViewCardDataRes {

    private EchartOption viewData;
}
