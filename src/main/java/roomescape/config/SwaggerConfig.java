package roomescape.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SecurityScheme(
        name = "jwtTokenCookieAuth",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.COOKIE,
        paramName = "token",
        description = "JWT 토큰을 쿠키에 저장하여 인증하는 방식"
)
@Configuration
public class SwaggerConfig {

    public static final String JWT_TOKEN_COOKIE_AUTH = "jwtTokenCookieAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("방탈출 예약 관리 사이트 API 문서🔑🚪")
                .description("방탈출 예약, 예약 목록 조회, 예약 취소, 대기를 위해 필요한 api를 정리합니다.")
                .version("1.0.0");
    }
}
