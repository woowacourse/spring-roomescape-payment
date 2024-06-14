package roomescape.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("방탈출 관리 프로그램 API")
                .description("방탈출 예약 및 관리를 위한 다양한 편의 기능을 제공합니다.")
                .version("1.0.0"));
    }
}
