package com.isxcode.star.api.view.pojo.res;

import com.isxcode.star.api.view.pojo.dto.ViewData;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetViewCardDataRes {

	private ViewData viewData;
}
