package com.isxcode.star.api.form.pojos.res;

import com.isxcode.star.api.form.pojos.dto.DataBodyDto;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PageDataRes {

	private List<Map<String, DataBodyDto>> data;

	private Integer count;
}
