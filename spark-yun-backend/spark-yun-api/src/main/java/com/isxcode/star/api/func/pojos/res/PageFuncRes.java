package com.isxcode.star.api.func.pojos.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageFuncRes {

	private String id;

	private String type;

	private String fileId;

	private String funcName;

	private String className;

	private String resultType;
}
