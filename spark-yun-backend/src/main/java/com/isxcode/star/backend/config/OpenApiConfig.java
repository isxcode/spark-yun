package com.isxcode.star.backend.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
  info = @Info(
    title = "至轻云",
    description = "基于spark打造超轻量级批处理大数据平台",
    version = "0.0.1"
  ),
  externalDocs = @ExternalDocumentation(
    url = "https://zhiqingyun.isxcode.com",
    description = "至轻云官方文档"
  ),
  servers = {
    @Server(
      url = "http://localhost:8080",
      description = "本地环境"
    ),
    @Server(
      url = "http://localhost:30204",
      description = "本地docker环境"
    ),
    @Server(
      url = "http://101.132.135.228:30204",
      description = "远程开发环境"
    )
  })
public class OpenApiConfig {

}
