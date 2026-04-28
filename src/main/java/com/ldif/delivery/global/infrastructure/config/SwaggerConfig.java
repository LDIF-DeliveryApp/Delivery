package com.ldif.delivery.global.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Delivery API")
                        .description("Delivery 프로젝트 API 문서")
                        .version("v1.0.0"))

                // JWT Authorize 버튼 설정
                .components(new Components()
                        .addSecuritySchemes(jwtSchemeName,
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )

                // Filter에서 처리하는 로그인 API를 Swagger에 수동 등록
                .path("/api/v1/auth/login",
                        new PathItem().post(new Operation()
                                .tags(List.of("auth-controller-v-1"))
                                .summary("로그인")
                                .description("username과 password로 로그인하고 JWT 토큰을 발급받습니다.")
                                .requestBody(new RequestBody()
                                        .required(true)
                                        .content(new Content()
                                                .addMediaType("application/json",
                                                        new MediaType().schema(new ObjectSchema()
                                                                .addProperty("username", new Schema<>().type("string").example("testUser"))
                                                                .addProperty("password", new Schema<>().type("string").example("password1234"))
                                                        )
                                                )
                                        )
                                )
                                .responses(new io.swagger.v3.oas.models.responses.ApiResponses()
                                        .addApiResponse("200", new ApiResponse()
                                                .description("로그인 성공")
                                                .content(new Content()
                                                        .addMediaType("application/json",
                                                                new MediaType().schema(new ObjectSchema()
                                                                        .addProperty("status", new Schema<>().type("integer").example(200))
                                                                        .addProperty("message", new Schema<>().type("string").example("SUCCESS"))
                                                                        .addProperty("data", new ObjectSchema()
                                                                                .addProperty("token", new Schema<>().type("string").example("eyJhbGciOiJIUzI1NiJ9..."))
                                                                                .addProperty("username", new Schema<>().type("string").example("testUser"))
                                                                                .addProperty("role", new Schema<>().type("string").example("CUSTOMER"))
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                        .addApiResponse("401", new ApiResponse()
                                                .description("로그인 실패"))
                                )
                        )
                );
    }
}