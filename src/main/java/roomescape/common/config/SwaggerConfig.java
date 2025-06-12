package roomescape.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI roomescapeOpenAPI() {
        final String title = "Roomescape API Docs";
        final String description = "방탈출 예약 서비스 API 문서";

        final Info info = new Info()
                .title(title)
                .description(description)
                .version("1.0.0");

        return new OpenAPI().info(info);
    }
}
