package roomescape.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("users")
                .packagesToScan("roomescape.controller")
                .pathsToExclude("/admin/**")
                .addOpenApiCustomizer(openApi -> openApi.info(apiInfo()))
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .packagesToScan("roomescape.controller")
                .pathsToMatch("/admin/**")
                .addOpenApiCustomizer(openApi -> openApi.info(apiInfo()))
                .build();
    }

    private Info apiInfo() {
        return new Info()
                .title("방탈출 예약 페이지 API")
                .description("방탈출 예약 페이지에서 호출되는 api 명세")
                .version("1.0.0");
    }
}
