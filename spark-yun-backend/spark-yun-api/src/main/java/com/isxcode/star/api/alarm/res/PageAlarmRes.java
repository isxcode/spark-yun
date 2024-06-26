package com.isxcode.star.api.alarm.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.star.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageAlarmRes {

	private String id;

	private String name;

	private String remark;

	private String msgType;

	private String status;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime createDateTime;
}
