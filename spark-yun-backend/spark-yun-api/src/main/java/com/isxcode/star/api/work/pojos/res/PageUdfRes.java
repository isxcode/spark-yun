package com.isxcode.star.api.work.pojos.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageUdfRes {

	private String id;

	private String type;

	private String udfFileId;

	private String funcName;

	private String className;

	private String resultType;

	private Boolean status;
}
