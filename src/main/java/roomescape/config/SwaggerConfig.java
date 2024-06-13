package roomescape.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@OpenAPIDefinition(
        info = @Info(title = "방탈출 API 명세서")
)
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("ALL")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public GroupedOpenApi reservationApi() {
        return GroupedOpenApi.builder()
                .group("예약")
                .pathsToMatch("/reservations/**", "/admin/reservations/**")
                .build();
    }

    @Bean
    public GroupedOpenApi waitingApi() {
        return GroupedOpenApi.builder()
                .group("예약대기")
                .pathsToMatch("/waiting/**", "/admin/waitings/**")
                .build();
    }

    @Bean
    public GroupedOpenApi themeApi() {
        return GroupedOpenApi.builder()
                .group("테마")
                .pathsToMatch("/themes/**")
                .build();
    }

    @Bean
    public GroupedOpenApi memberApi() {
        return GroupedOpenApi.builder()
                .group("멤버")
                .pathsToMatch("/members/**")
                .build();
    }
    @Bean
    public GroupedOpenApi reservationTimeApi() {
        return GroupedOpenApi.builder()
                .group("예약시각")
                .pathsToMatch("/times/**")
                .build();
    }

    @Bean
    public GroupedOpenApi loginApi() {
        return GroupedOpenApi.builder()
                .group("로그인")
                .packagesToScan("roomescape.controller.auth")
                .build();
    }
}
