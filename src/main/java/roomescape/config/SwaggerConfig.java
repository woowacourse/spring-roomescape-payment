package roomescape.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import roomescape.auth.AuthConstants;

@Configuration
public class SwaggerConfig {
    @Value("${apiVersion}")
    private static String apiVersion;

    @Bean
    public OpenAPI openAPI() {
        Components cookieAuthComponent = new Components()
                .addSecuritySchemes(AuthConstants.AUTH_COOKIE_NAME, apiAuth());

        return new OpenAPI()
                .info(apiInfo())
                .components(cookieAuthComponent)
                .addSecurityItem(new SecurityRequirement().addList(AuthConstants.AUTH_COOKIE_NAME));
    }

    private SecurityScheme apiAuth() {
        return new SecurityScheme().type(Type.APIKEY)
                .in(In.COOKIE)
                .name(AuthConstants.AUTH_COOKIE_NAME);
    }

    private Info apiInfo() {
        return new Info()
                .title("RoomEscape API")
                .description("방탈출 예약 서비스 API 입니다.")
                .version(apiVersion);
    }
}
