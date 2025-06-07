package roomescape.support.documentation;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "방탈출 API",
                description = "방탈출 미션 API 문서",
                version = "v1")
)
@Configuration
public class SwaggerConfig {

//    @Bean
//    public GroupedOpenApi adminApi() {
//        String[] paths = {"/admin/**"};
//
//        return GroupedOpenApi
//                .builder()
//                .group("관리자 전용 api")
//                .pathsToMatch(paths)
//                .build();
//    }
//
//    @Bean
//    public GroupedOpenApi loginApi() {
//        String[] paths = {"/login", "/login/**"};
//
//        return GroupedOpenApi
//                .builder()
//                .group("로그인 api")
//                .pathsToMatch(paths)
//                .build();
//    }
//
//    @Bean
//    public GroupedOpenApi memberApi() {
//        String[] paths = {"/member/**"};
//
//        return GroupedOpenApi
//                .builder()
//                .group("회원 관련 api")
//                .pathsToMatch(paths)
//                .build();
//    }
//
//    @Bean
//    public GroupedOpenApi myReservationApi() {
//        String[] paths = {"/reservations-mine/**"};
//
//        return GroupedOpenApi
//                .builder()
//                .group("회원 전용 예약 관련 api")
//                .pathsToMatch(paths)
//                .build();
//    }
//
//    @Bean
//    public GroupedOpenApi reservationApi() {
//        String[] paths = {"/reservations/**"};
//
//        return GroupedOpenApi
//                .builder()
//                .group("예약 관련 api")
//                .pathsToMatch(paths)
//                .build();
//    }
//
//    @Bean
//    public GroupedOpenApi reservationTimeApi() {
//        String[] paths = {"/times/**"};
//
//        return GroupedOpenApi
//                .builder()
//                .group("예약 시각 관련 api")
//                .pathsToMatch(paths)
//                .build();
//    }
//
//    @Bean
//    public GroupedOpenApi themeApi() {
//        String[] paths = {"/themes/**"};
//
//        return GroupedOpenApi
//                .builder()
//                .group("테마 관련 api")
//                .pathsToMatch(paths)
//                .build();
//    }
//
//    @Bean
//    public GroupedOpenApi waitingApi() {
//        String[] paths = {"/waiting/**"};
//
//        return GroupedOpenApi
//                .builder()
//                .group("웨이팅 관련 api")
//                .pathsToMatch(paths)
//                .build();
//    }


}
