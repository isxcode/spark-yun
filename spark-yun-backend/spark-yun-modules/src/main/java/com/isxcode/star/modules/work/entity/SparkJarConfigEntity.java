package com.isxcode.star.modules.work.entity;

import lombok.Data;

import java.util.List;

@Data
public class SparkJarConfigEntity {

	private String jarFileId;

	private List<String> libFileIds;

	private String mainClass;

	private String args;

}