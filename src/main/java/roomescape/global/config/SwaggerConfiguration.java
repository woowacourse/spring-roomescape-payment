package roomescape.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                        .title("Room Escape API")
                        .version("v1.0")
                        .description("방탈출 예약 시스템 API 명세")
                );
    }

    @Bean
    public GroupedOpenApi auth() {
        return GroupedOpenApi.builder()
                .group("인증 및 사용자")
                .pathsToMatch("/signin", "/login/**", "/logout", "/members")
                .build();
    }

    @Bean
    public GroupedOpenApi reservationTime() {
        return GroupedOpenApi.builder()
                .group("방탈출 예약 시간")
                .pathsToMatch("/times/**")
                .build();
    }

    @Bean
    public GroupedOpenApi theme() {
        return GroupedOpenApi.builder()
                .group("방탈출 예약 테마")
                .pathsToMatch("/themes/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminReservation() {
        return GroupedOpenApi.builder()
                .group("관리자 방탈출 예약")
                .pathsToMatch("/admin/reservations/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminWaiting() {
        return GroupedOpenApi.builder()
                .group("관리자 방탈출 대기")
                .pathsToMatch("/admin/waitings/**")
                .build();
    }

    @Bean
    public GroupedOpenApi memberReservation() {
        return GroupedOpenApi.builder()
                .group("사용자 방탈출 예약")
                .pathsToMatch("/reservations/**")
                .build();
    }

    @Bean
    public GroupedOpenApi memberWaiting() {
        return GroupedOpenApi.builder()
                .group("사용자 방탈출 대기")
                .pathsToMatch("/waitings/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminSearch() {
        return GroupedOpenApi.builder()
                .group("관리자 예약 검색")
                .pathsToMatch("/admin/search")
                .build();
    }

    @Bean
    public GroupedOpenApi memberMyReservations() {
        return GroupedOpenApi.builder()
                .group("사용자 방탈출 대기")
                .pathsToMatch("/members/reservations-mine/**")
                .build();
    }
}
