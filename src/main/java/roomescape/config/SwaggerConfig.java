package roomescape.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("방탈출 예약 사이트 서버 명세")
                .version("v0.0.1")
                .description("방탈출 예약 사이트의 서버 명세입니다.");
        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}
