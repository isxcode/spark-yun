package com.isxcode.spark.api.view.res;

import com.isxcode.spark.api.view.dto.EchartOption;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetViewCardDataRes {

    private EchartOption viewData;
}
