package roomescape.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RoomEscape API Docs")
                        .version("1.0")
                        .description("방탈출 예약 홈페이지 API 문서입니다."))
                .addSecurityItem(new SecurityRequirement().addList("cookieAuth"))
                .components(createSecurityScheme());
    }

    private Components createSecurityScheme() {
        return new Components().addSecuritySchemes("cookieAuth", new SecurityScheme()
                .name("cookieAuth")
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .description("'token=token123'와 같이 인증 정보를 입력해주세요."));
    }
}
