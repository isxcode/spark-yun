package com.isxcode.spark.api.work.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 和代理请求的统一返回.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentResponse {

    private String appStatus;

    private String message;

    private String log;

    private List<List<String>> data;

    private String appId;

    private String trackingUrl;

    private String instanceId;
}
