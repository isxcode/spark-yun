package com.isxcode.star.api.alarm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MessageConfig {

    @Schema(title = "阿里短信region", example = "cn-hangzhou")
    private String region;

    @Schema(title = "阿里云短信accessKeyId")
    private String accessKeyId;

    @Schema(title = "阿里云短信accessKeySecret")
    private String accessKeySecret;

    @Schema(title = "阿里云短信模版", example = "SMS_154950909")
    private String templateCode;

    @Schema(title = "阿里短信signName", example = "至轻云")
    private String signName;

    @Schema(title = "阿里短信templateParam", example = "cn-hangzhou")
    private String contentTemplate;

    private String host;

    private int port;

    private String username;

    private String password;

    private String subject;
}
