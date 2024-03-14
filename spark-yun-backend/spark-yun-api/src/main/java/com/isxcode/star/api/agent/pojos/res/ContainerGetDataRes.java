package com.isxcode.star.api.agent.pojos.res;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ContainerGetDataRes {

	private List<List<String>> date;

	private String code;
}
