package roomescape.common.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SecurityScheme(
        name = "cookieAuth",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.COOKIE,
        paramName = "access_token",
        description = "쿠키에 담긴 JWT 기반 인증 및 인가"
)
@Configuration
public class SpringDocsConfig {

    @Value(value = "${springdoc.server-url}")
    private String url;

    @Bean
    public OpenAPI openAPI() {
        Contact contact = new Contact()
                .name("레오")
                .email("rlawnsdud920@navr.com");
        Info info = new Info()
                .title("방탈출 API")
                .version("v1")
                .description("우아한테크코스 7기 레벨2 방탈출 예약 관리 시스템")
                .contact(contact);
        return new OpenAPI()
                .info(info)
                .addServersItem(new Server().url(url));
    }
}
