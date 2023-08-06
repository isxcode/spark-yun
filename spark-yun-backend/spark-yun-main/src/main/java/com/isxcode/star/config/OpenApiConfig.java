package com.isxcode.star.config;

import com.isxcode.star.backend.api.base.constants.SecurityConstants;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(title = "至轻云", description = "基于spark打造超轻量级批处理大数据平台", version = "latest"),
    externalDocs =
        @ExternalDocumentation(url = "https://zhiqingyun.isxcode.com", description = "至轻云官方文档"),
    servers = {
      @Server(url = "http://localhost:8080", description = "本地环境"),
      @Server(url = "http://localhost:30204", description = "本地docker环境"),
      @Server(url = "http://101.132.135.228:30211", description = "远程开发环境")
    })
@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenApi() {

    SecurityScheme basicAuthScheme =
        new SecurityScheme()
            .type(SecurityScheme.Type.APIKEY)
            .in(SecurityScheme.In.HEADER)
            .description("输入用户token")
            .scheme("basic")
            .name(SecurityConstants.HEADER_AUTHORIZATION);

    SecurityRequirement basicAuthRequirement = new SecurityRequirement().addList("tokenAuth");

    return new OpenAPI()
        .components(new Components().addSecuritySchemes("tokenAuth", basicAuthScheme))
        .addSecurityItem(basicAuthRequirement);
  }
}
