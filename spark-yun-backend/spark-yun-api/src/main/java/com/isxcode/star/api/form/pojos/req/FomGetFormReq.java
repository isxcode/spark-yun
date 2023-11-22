package com.isxcode.star.api.form.pojos.req;

import com.isxcode.star.api.form.pojos.dto.FomComponentDto;
import lombok.Data;

import java.util.List;

@Data
public class FomGetFormReq {

	private String name;

	private String datasourceId;

	private String mainTable;

	private String insertSql;

	private String deleteSql;

	private String updateSql;

	private String selectSql;

	private List<FomComponentDto> components;
}
