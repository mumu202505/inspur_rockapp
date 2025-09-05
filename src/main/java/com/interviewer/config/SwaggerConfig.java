package com.interviewer.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("API文档").version("1.0"))
                .components(new Components()
                        .addSecuritySchemes("CustomTokenAuth",  // 安全方案名称（可自定义）
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY) // 类型改为APIKEY
                                        .in(SecurityScheme.In.HEADER)     // 指定在Header中传递
                                        .name("Passport")             // 您的自定义Header名称
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("CustomTokenAuth"));
    }
}
