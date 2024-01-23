package com.isxcode.star.api.api.pojos.res;

import lombok.Data;

@Data
public class GetApiRes {

	private String id;

	private String name;

	private String path;

	private String remark;

	private String apiType;

	private String tokenType;

	private String reqHeader;

	private String reqBody;

	private String apiSql;

	private String resBody;

	private String datasourceId;

	private Boolean pageType;
}