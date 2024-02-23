package com.isxcode.star.api.work.pojos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JarJobConfig {

	private String jarFileId;

	private List<String> libFileList;

	private String mainClass;

	private String[] args;

	private String appName;
}