package roomescape.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${server.url.local}")
    private String localUrl;

    @Value("${server.url.prod}")
    private String prodUrl;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("방탈출 예약 API")
                        .version("v1")
                        .description("방탈출 예약 관련 API 명세서"))
                .servers(List.of(
                        new Server().url(localUrl).description("로컬 서버"),
                        new Server().url(prodUrl).description("운영 서버")
                ))
                .components(new Components()
                        .addSecuritySchemes("cookieAuthorization",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.COOKIE)
                                        .name("token")
                                        .description("""
                                                Swagger에서는 인증 쿠키를 자동으로 설정할 수 없습니다. \s
                                                인증 카테고리의 /login 엔드포인트를 통해 쿠키가 설정된 상태에서 테스트하거나, \s
                                                브라우저 콘솔에서 다음 명령어로 쿠키를 직접 설정한 후 테스트해 주세요: \s
                                                document.cookie = "token=${jwt_token}; path=/";
                                                """)
                        ))
                .addSecurityItem(new SecurityRequirement().addList("cookieAuthorization"));
    }
}
