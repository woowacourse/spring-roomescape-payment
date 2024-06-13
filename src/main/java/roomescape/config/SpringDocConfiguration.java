package roomescape.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import roomescape.customizer.AuthOperationCustomizer;

@Configuration
public class SpringDocConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        Info info = new Info()
                .version("v1.0.0")
                .title("방탈출 API")
                .description("폴라의 도키도키 방탈출 예약 API");

        return new OpenAPI()
                .info(info);
    }

    @Bean
    public GroupedOpenApi memberReservation() {
        return GroupedOpenApi.builder()
                .group("멤버 예약")
                .pathsToMatch("/**")
                .pathsToExclude("/admin/**", "/login/**", "/themes/**", "/times/**", "/members/**")
                .addOperationCustomizer(new AuthOperationCustomizer())
                .build();
    }

    @Bean
    public GroupedOpenApi adminReservation() {
        return GroupedOpenApi.builder()
                .group("관리자 예약")
                .pathsToMatch("/admin/**", "/themes/**", "/times/**")
                .build();
    }

    @Bean
    public GroupedOpenApi nonMemberReservation() {
        return GroupedOpenApi.builder()
                .group("비회원 예약")
                .pathsToMatch("/login/**", "/themes/popular/**")
                .build();
    }
}
