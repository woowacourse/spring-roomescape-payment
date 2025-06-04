package roomescape.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("방탈출 API Document")
                .version("1.0")
                .description(
                        "이 API 문서는 방탈출 API를 사용하는 방법을 설명합니다.\n");

        return new OpenAPI()
                .info(info)
                .addServersItem(new Server().url("http://localhost:8080"));
    }
}
