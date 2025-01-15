package com.isxcode.star.api.work.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JarJobConfig {

    private String jarFileId;

    private String mainClass;

    private String[] args;

    private String appName;
}
