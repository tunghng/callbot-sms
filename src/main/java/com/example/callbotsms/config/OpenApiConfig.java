package com.example.callbotsms.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {

    @Value("${server.port}")
    private String port;

    @Value("${module.version}")
    private String moduleVer;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .servers(Arrays.asList(
                        new Server().url("https://erp.innovation.com.vn"),
                        new Server().url("http://localhost:" + port)
                ))
                .info(new Info()
                        .title("SSO Service API")
                        .description("Single Sign-On Service for Industrial Management")
                        .contact(new Contact()
                                .email("ttuquang@tma.com.vn")
                                .name("Tran Tu Quang")
                        ).license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                        .version(moduleVer));
    }

}
