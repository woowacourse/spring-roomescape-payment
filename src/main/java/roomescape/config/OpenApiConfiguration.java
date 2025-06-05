package roomescape.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SecurityScheme(name = "cookieAuth",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.COOKIE,
        paramName = "token"
)
@Schema(name = "ValidationError",
        description = "입력값 검증 실패 응답",
        example = """
                [
                    {
                        "field": "필드명1",
                        "message": "검증 실패 메시지1"
                    },
                    {
                        "field": "필드명2",
                        "message": "검증 실패 메시지2"
                    }
                ]
                """
)
@OpenAPIDefinition(info = @Info(title = "방탈출 예약 API", version = "1.0"))
@Configuration
public class OpenApiConfiguration {

    @Bean
    public GroupedOpenApi groupTotal() {
        return GroupedOpenApi.builder()
                .group("전체")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public GroupedOpenApi groupReservation() {
        return GroupedOpenApi.builder()
                .group("예약")
                .pathsToMatch("/reservations/**")
                .build();
    }

    @Bean
    public GroupedOpenApi groupAdmin() {
        return GroupedOpenApi.builder()
                .group("관리자")
                .pathsToMatch("/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi groupReservationTime() {
        return GroupedOpenApi.builder()
                .group("예약 가능 시간")
                .pathsToMatch("/times/**")
                .build();
    }

    @Bean
    public GroupedOpenApi groupTheme() {
        return GroupedOpenApi.builder()
                .group("방탈출 테마")
                .pathsToMatch("/themes/**")
                .build();
    }

    @Bean
    public GroupedOpenApi groupMember() {
        return GroupedOpenApi.builder()
                .group("사용자")
                .pathsToMatch("/members/**")
                .build();
    }

    @Bean
    public GroupedOpenApi groupAuth() {
        return GroupedOpenApi.builder()
                .group("인증")
                .pathsToMatch("/login/**", "/logout")
                .build();
    }
}
