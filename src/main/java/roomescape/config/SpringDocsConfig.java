package roomescape.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocsConfig {

    @Bean
    public OpenAPI openAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.description("개발 환경 서버 URL");

        Info info = new Info()
                .title("방탈출 예약 API")
                .version("v1.0.0")
                .description("방탈출 예약 서비스 서버 API 문서입니다. 문의는 roomescape@woowahan.net으로 해주세요.");

        SecurityScheme securityScheme = new SecurityScheme()
                .type(Type.APIKEY)
                .in(In.COOKIE)
                .name("token")
                .description("Token stored in cookies");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer))
                .components(new Components().addSecuritySchemes("Bearer auth", securityScheme))
                .security(List.of(securityRequirement));
    }
}
