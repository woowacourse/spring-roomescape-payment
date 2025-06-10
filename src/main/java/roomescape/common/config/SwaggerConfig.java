package roomescape.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${app.version}")
    private String appVersion;

    @Bean
    public OpenAPI springShopOpenAPI() {
        final String title = "RoomEscape Application Swagger";
        final String description = "방탈출 서비스의 API 문서입니다.";

        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .description(description)
                        .version(appVersion)
                        .contact(new Contact()
                                .name("MJ")
                                .email("mj@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html"))
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("로컬 서버"),
                        new Server()
                                .url("http://3.36.117.160:8080")
                                .description("프로덕션 서버")
                ));
    }
}
