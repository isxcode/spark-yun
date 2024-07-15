package com.isxcode.star.common.config;

import com.isxcode.star.backend.api.base.constants.SecurityConstants;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(info = @Info(title = "至轻云", description = "企业级大数据计算平台"),
    servers = {@Server(url = "http://localhost:8080", description = "本地环境"),
            @Server(url = "https://zhiqingyun-demo.isxcode.com", description = "远程环境")})
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi() {

        SecurityScheme basicAuthScheme =
            new SecurityScheme().type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER).description("输入用户token")
                .scheme("basic").name(SecurityConstants.HEADER_AUTHORIZATION);

        SecurityScheme basicTenantScheme =
            new SecurityScheme().type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER).description("输入租户tenant")
                .scheme("tenant").name(SecurityConstants.HEADER_TENANT_ID);

        SecurityRequirement basicAuthRequirement = new SecurityRequirement().addList("tenantAuth").addList("tokenAuth");
        return new OpenAPI().components(new Components().addSecuritySchemes("tokenAuth", basicAuthScheme)
            .addSecuritySchemes("tenantAuth", basicTenantScheme)).addSecurityItem(basicAuthRequirement);
    }
}
