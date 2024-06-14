package com.isxcode.star.api.view.pojo.res;

import com.isxcode.star.api.view.pojo.dto.DataSql;
import com.isxcode.star.api.view.pojo.dto.EchartOption;
import lombok.Data;

@Data
public class GetViewCardRes {

	private String id;

	private String name;

	private String type;

	private String datasourceId;

	private DataSql dataSql;

	private Object webConfig;

	private EchartOption exampleData;
}
