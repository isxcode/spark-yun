package com.isxcode.star.api.form.pojos.res;

import com.isxcode.star.api.form.pojos.dto.FomComponentDto;
import lombok.Data;

import java.util.List;

@Data
public class FomGetFormRes {

  private String id;

  private String name;

	private String datasourceId;

	private String mainTable;

	private List<FomComponentDto> components;
}
