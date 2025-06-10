package roomescape.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
            .addSecurityItem(new SecurityRequirement().addList("CookieAuth"))
            .components(new Components()
                .addSecuritySchemes("CookieAuth", createCookieSchema()))
            .info(new Info().title("방탈출 예약 시스템"));
    }

    private SecurityScheme createCookieSchema() {
        return new SecurityScheme()
            .type(Type.APIKEY)
            .in(SecurityScheme.In.COOKIE)
            .name("token");
    }
}
