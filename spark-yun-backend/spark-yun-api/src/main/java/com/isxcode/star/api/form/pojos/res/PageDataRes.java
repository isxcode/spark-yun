package com.isxcode.star.api.form.pojos.res;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PageDataRes {

	private List<Map<String, Object>> data;

	private Integer count;
}
