package roomescape.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(info());
    }

    private Info info() {
        return new Info().title("Roomescape Application")
                .description("API Documentation for Roomescape Application")
                .version("1.0.0");
    }
}
