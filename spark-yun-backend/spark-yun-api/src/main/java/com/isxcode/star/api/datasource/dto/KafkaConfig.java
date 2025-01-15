package com.isxcode.star.api.datasource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class KafkaConfig {

    @Schema(title = "实时同步的时候添加")
    private String topic;

    @Schema(title = "添加数据源的时候添加")
    private String bootstrapServers;

    @Schema(title = "实时同步的时候添加")
    private String startingOffsets;

    @Schema(title = "实时时间")
    private String durationTime;

    @Schema(title = "group.id")
    private String groupIdPrefix;

    private Map<String, String> properties;
}
