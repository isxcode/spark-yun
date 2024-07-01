package com.isxcode.star.api.view.pojo.res;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.star.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PageViewRes {

	private String id;

	private String name;

	private String status;

	private String remark;

	private String createBy;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime createDateTime;
}
