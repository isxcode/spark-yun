package com.isxcode.star.api.cluster.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScpFileEngineNodeDto {

    private String username;

    private String passwd;

    private String port;

    private String host;
}
