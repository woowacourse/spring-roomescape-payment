package roomescape.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("cookieAuth"))
                .components(new Components()
                        .addSecuritySchemes("cookieAuth", new SecurityScheme()
                                .name("cookieAuth")
                                .type(Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("Cookie")
                                .description("'Idea-2585f9c0=8867cf15-27ba-43b0-b129-3e4bbe9ca816; token=token123'와 같이 인증 정보를 입력해주세요.")));
    }
}
