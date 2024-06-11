package roomescape.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(getInfo())
                .addSecurityItem(getSecurityRequirement())
                .components(getComponents());
    }

    private Info getInfo() {
        return new Info().title("방탈출 사용자 예약 페이지")
                .description("방탈출 예약 API 문서입니다.")
                .version("1.0.0");
    }

    private SecurityRequirement getSecurityRequirement() {
        return new SecurityRequirement().addList("cookieAuth");
    }

    private Components getComponents() {
        return new Components()
                .addSecuritySchemes("cookieAuth", getSecurityScheme());
    }

    private SecurityScheme getSecurityScheme() {
        return new SecurityScheme()
                .type(Type.APIKEY)
                .in(In.COOKIE)
                .name("token")
                .description("로그인 후 발급받은 토큰을 입력하세요.");
    }
}
