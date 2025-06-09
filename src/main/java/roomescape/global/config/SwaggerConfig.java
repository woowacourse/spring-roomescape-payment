package roomescape.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("방탈출 예약 시스템")
                .version("v1.0")
                .description("방탈출 예약 시스템 API 문서");

        return new OpenAPI()
                .info(info);
    }
}
