package com.isxcode.star.api.alarm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MessageConfig {

	private String accessKey;

	@Schema(title = "阿里云短信模版", example = "SMS_154950909")
	private String smsTemplate;
}
