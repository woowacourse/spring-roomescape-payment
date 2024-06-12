package roomescape.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Objects;
import org.springdoc.core.customizers.RouterOperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

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
    public RouterOperationCustomizer reservationsRouterOperationCustomizer() {
        return (routerOperation, handlerMethod) -> {
            String path = routerOperation.getPath();
            if (Objects.equals("/reservations", path) && RequestMethod.GET == routerOperation.getMethods()[0]) {
                String[] params = routerOperation.getParams();
                if (params.length != 0) {
                    StringBuilder pathBuilder = new StringBuilder();
                    pathBuilder.append(path + "?")
                            .append(params[0] + "=1&")
                            .append(params[1] + "=1&")
                            .append(params[2] + "=2024-05-01&")
                            .append(params[3] + "=2024-05-02");
                    routerOperation.setPath(pathBuilder.toString());
                }
            }
            return routerOperation;
        };
    }
}
