package roomescape.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(title = "방탈출 API", version = "v1",
                description = "우아한테크코스 7기 레벨2 방탈출 예약 관리 시스템",
                contact = @Contact(name = "레오", email = "rlawnsdud920@gmail.com")),
        servers = @Server(url = "http://localhost:8080/")
)
@SecurityScheme(
        name = "cookieAuth",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.COOKIE,
        paramName = "access_token"
)
public class SpringDocsConfig {
}
