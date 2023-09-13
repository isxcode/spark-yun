package com.isxcode.star.agent.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spark-yun")
@EnableConfigurationProperties(SparkYunAgentProperties.class)
public class SparkYunAgentProperties {

	/**
	 * 提交作业的超时时间，默认60s
	 */
	private Integer submitTimeout = 120;
}
