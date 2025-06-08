package roomescape.infrastructure.config;

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
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Room Escape Payment API")
                .version("1.0.0")
                .description("방탈출 예약 및 결제 시스템 API 문서");

        SecurityScheme cookieScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("Cookie");

        Components components = new Components()
                .addSecuritySchemes("cookieAuth", cookieScheme);
        
        SecurityRequirement requirement = new SecurityRequirement()
                .addList("cookieAuth");

        return new OpenAPI()
                .info(info)
                .components(components)
                .addSecurityItem(requirement);
    }
}
