package roomescape.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springdoc.core.customizers.RouterOperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(getInfo())
                .components(new Components().addSecuritySchemes("bearer-key",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"));
    }

    private Info getInfo() {
        return new Info().title("방탈출 예약 API 문서")
                .description("방탈출 예약 API 문서입니다.")
                .version("1.0.0");
    }

    @Bean
    public RouterOperationCustomizer routerOperationCustomizer() {
        return (routerOperation, handlerMethod) -> {
            if (routerOperation.getParams().length > 0) {
                String params = Arrays.stream(routerOperation.getParams())
                        .collect(Collectors.joining("&"));
                routerOperation.setPath(routerOperation.getPath() + "?" + params);
            }
            return routerOperation;
        };
    }
}
