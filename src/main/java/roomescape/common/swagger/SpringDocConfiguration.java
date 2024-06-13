package roomescape.common.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(info = @io.swagger.v3.oas.annotations.info.Info(title = "방탈출 API", version = "v1"))
@Configuration
public class SpringDocConfiguration {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("방탈출 API")
                .version("v1.0")
                .description("방탈출 API 명세서입니다.");

        Components cookieComponent = new Components()
                .addSecuritySchemes("쿠키 인증 토큰", new SecurityScheme()
                        .type(Type.APIKEY)
                        .in(In.COOKIE)
                        .name("token"));

        return new OpenAPI()
                .info(info)
                .components(cookieComponent)
                .addSecurityItem(new SecurityRequirement().addList("쿠키 인증 토큰"));
    }

    @Bean
    public OperationCustomizer addCommonErrorResponses() {
        return new AuthOperationCustomizer();
    }
}
