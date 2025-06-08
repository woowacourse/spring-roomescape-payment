package roomescape.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI roomEscapeOpenAPI() {
        String title = "방탈출";
        String description = "방탈출 서비스 API 문서입니다.";

        Info info = new Info().title(title).description(description).version("1.0.0");
        return new OpenAPI().info(info);
    }
}