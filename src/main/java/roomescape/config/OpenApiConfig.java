package roomescape.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        Info info = new Info()
                .version("v1.0.0")
                .title("Room Escape API")
                .description("방탈출 예약을 위한 API입니다.");

        return new OpenAPI()
                .info(info);
    }
}
