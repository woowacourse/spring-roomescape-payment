package roomescape.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .servers(List.of(new Server().url("http://localhost:8080")
                        .description("local server")))
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("방탈출 웹 서비스")
                .version("1.0.0");
    }
}
