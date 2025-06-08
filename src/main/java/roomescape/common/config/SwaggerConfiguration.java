package roomescape.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .version("v1.0.0")
                .title("우테코 레벨2 방탈출 미션 API")
                .description("안녕하세요 에드입니다.");

        return new OpenAPI()
                .info(info);
    }

    @Bean
    public GroupedOpenApi auth() {
        return GroupedOpenApi.builder()
                .group("인증 및 사용자")
                .pathsToMatch("/login/**", "/logout", "/members", "/admin/members")
                .build();
    }

    @Bean
    public GroupedOpenApi userTheme() {
        return GroupedOpenApi.builder()
                .group("사용자-테마")
                .pathsToMatch("/themes/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminTheme() {
        return GroupedOpenApi.builder()
                .group("관리자-테마")
                .pathsToMatch("/admin/themes/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userTime() {
        return GroupedOpenApi.builder()
                .group("사용자-예약시간")
                .pathsToMatch("/times/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminTime() {
        return GroupedOpenApi.builder()
                .group("관리자-예약시간")
                .pathsToMatch("/admin/times/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userReservation() {
        return GroupedOpenApi.builder()
                .group("사용자-예약")
                .pathsToMatch("/reservations/**", "/payments/prepay")
                .build();
    }

    @Bean
    public GroupedOpenApi adminReservation() {
        return GroupedOpenApi.builder()
                .group("관리자-예약")
                .pathsToMatch("/admin/reservations/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userWaiting() {
        return GroupedOpenApi.builder()
                .group("사용자-예약대기")
                .pathsToMatch("/waitings/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminWaiting() {
        return GroupedOpenApi.builder()
                .group("관리자-예약대기")
                .pathsToMatch("/admin/waitings/**")
                .build();
    }


}
