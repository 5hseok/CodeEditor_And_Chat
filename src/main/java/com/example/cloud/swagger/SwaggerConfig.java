package com.example.cloud.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
//	@Bean
//	public OpenAPI openAPI() {
//		String jwt = "JWT";
//		SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
//		Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
//			.name(jwt)
//			.type(SecurityScheme.Type.HTTP)
//			.scheme("bearer")
//			.bearerFormat("JWT")
//		);
//		return new OpenAPI()
//			.components(new Components())
//			.info(apiInfo())
//			.addSecurityItem(securityRequirement)
//			.components(components);
//	}

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.info(new Info()
						.version("1.0.0")
						.title("Codeable API 명세서")
						.description("API 명세서"));

	}
}
