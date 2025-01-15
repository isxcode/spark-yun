package com.isxcode.star.api.work.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ApiWorkConfig {

    @Schema(title = "作业唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
    private String workId;

    @Schema(title = "接口请求url", example = "http(s)://zhiqingyun-demo.isxcode.com/xxxx/xxxx")
    private String requestUrl;

    @Schema(title = "接口请求类型", example = "POST/GET")
    private String requestType;

    @Schema(title = "接口请求参数")
    private List<ApiWorkValueDto> requestParam;

    @Schema(title = "接口请求头")
    private List<ApiWorkValueDto> requestHeader;

    @Schema(title = "接口请求体")
    private String requestBody;

}
