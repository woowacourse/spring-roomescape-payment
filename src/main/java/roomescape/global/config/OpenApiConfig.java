package roomescape.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("cookieAuth"))
                .components(getComponents());
    }

    private Components getComponents() {
        return new Components().addSecuritySchemes("cookieAuth", new SecurityScheme()
                .name("cookieAuth")
                .type(Type.APIKEY)
                .in(In.COOKIE)
                .description("'Idea-2585f9c0=xxxx; token=token123'와 같이 인증 정보를 입력해주세요."));
    }
}
