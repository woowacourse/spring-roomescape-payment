package roomescape.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "미미의 방탈출 미션",
                description = "미미의 방탈출 미션 API 문서입니다.",
                version = "v1"),
        servers = {
                @Server(url = "https://배포하고 수정해야", description = "실서버")
        }
)
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi loginApi() {
        String[] paths = {"/login/**"};

        return GroupedOpenApi
                .builder()
                .group("로그인 api")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi memberApi() {
        String[] paths = {"/members/**"};

        return GroupedOpenApi
                .builder()
                .group("회원 api")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi adminReservationApi() {
        String[] paths = {"/admin/reservations/**"};

        return GroupedOpenApi
                .builder()
                .group("관리자 예약 api")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi reservationApi() {
        String[] paths = {"/reservations/**"};

        return GroupedOpenApi
                .builder()
                .group("사용자 예약 api")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi themeApi() {
        String[] paths = {"/themes/**"};

        return GroupedOpenApi
                .builder()
                .group("테마 api")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi timeApi() {
        String[] paths = {"/times/**"};

        return GroupedOpenApi
                .builder()
                .group("예약 시간대 api")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi waiteApi() {
        String[] paths = {"/waits/**"};

        return GroupedOpenApi
                .builder()
                .group("예약 대기 api")
                .pathsToMatch(paths)
                .build();
    }
}
