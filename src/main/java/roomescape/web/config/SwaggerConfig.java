package roomescape.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("방탈출 예약 API")
                        .description("방탈출 예약 및 대기를 하는 API 입니다.")
                        .version("1.0.0"));
    }
}
