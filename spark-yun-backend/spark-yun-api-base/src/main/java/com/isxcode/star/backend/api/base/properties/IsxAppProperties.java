package com.isxcode.star.backend.api.base.properties;

import java.util.List;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "isx-app")
@EnableConfigurationProperties(IsxAppProperties.class)
public class IsxAppProperties {

	/**
	 * 需要ADMIN权限访问路径.
	 */
	private List<String> adminUrl;

	/**
	 * 任何人都可以访问路径.
	 */
	private List<String> anonymousUrl;

	/**
	 * 匿名者权限都可以访问路径.
	 */
	private List<String> anonymousRoleUrl;

	/**
	 * aes密钥.
	 */
	private String aesSlat = "spark-yun";

	/**
	 * jwt密钥.
	 */
	private String jwtKey = "spark-yun";

	/**
	 * jwt超时.(分钟)
	 */
	private Integer expirationMin = 1440;

	/**
	 * 资源目录.
	 */
	private String resourcesPath = "/var/lib/zhiqingyun";

	/**
	 * 系统管理员admin密码.
	 */
	private String adminPasswd = "admin123";

	/**
	 * 开启用户操作日志
	 */
	private boolean logAdvice = false;

	/**
	 * 使用https协议访问接口.
	 */
	private boolean useSsl = false;

	/**
	 * 使用端口号访问接口.
	 */
	private boolean usePort = true;

	/**
	 * docker镜像部署模式.
	 */
	private boolean dockerMode = false;
}
