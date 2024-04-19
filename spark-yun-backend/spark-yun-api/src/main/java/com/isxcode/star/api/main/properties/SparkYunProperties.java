package com.isxcode.star.api.main.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spark-yun")
@EnableConfigurationProperties(SparkYunProperties.class)
public class SparkYunProperties {

	/** 可以让脚本临时复制的目录. */
	private String tmpDir = "/tmp";

	/** 代理默认端口号. */
	private String defaultAgentPort = "30177";
}
