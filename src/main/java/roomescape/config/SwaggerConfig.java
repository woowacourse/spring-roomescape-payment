package roomescape.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("API")
                .packagesToScan("roomescape.controller")
                .addOpenApiCustomizer(new OpenApiCustomizer() {
                    @Override
                    public void customise(OpenAPI openApi) {
                        openApi.info(apiInfo());
                    }
                })
                .build();
    }

    private Info apiInfo() {
        License license = new License();
        license.setName("라이센스");

        return new Info()
                .title("방탈출 API")
                .description("방탈출 결제 / 배포 4단계 문서화")
                .version("1")
                .license(license);
    }
}
