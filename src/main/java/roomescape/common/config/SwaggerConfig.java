package roomescape.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "cookieAuth";

    @Bean
    public OpenAPI roomEscapeOpenAPI() {
        String title = "RoomEscape Application Docs";
        String description = "방탈출 서비스 API 문서입니다.";

        Info info = new Info()
                .title(title)
                .description(description)
                .version("1.0.0");

        SecurityScheme cookieAuthScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("ACCESS_TOKEN")
                .description("JWT 토큰이 포함된 쿠키로 인증합니다.");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(SECURITY_SCHEME_NAME);

        return new OpenAPI()
                .info(info)
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, cookieAuthScheme))
                .addSecurityItem(securityRequirement);
    }
}
