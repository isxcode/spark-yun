package com.isxcode.star.api.api.pojos.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestApiRes {

	private int httpStatus;

	private Object body;

	private String msg;
}
