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

    public static final String SECURITY_SCHEME_NAME = "RESERVATION_TOKEN_SECURITY";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("방탈출 예약 프로그램 API 명세서")
                        .description("방탈출 예약 프로그램에서 사용되는 API 목록입니다."))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name("token")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER))
                );
    }
}
