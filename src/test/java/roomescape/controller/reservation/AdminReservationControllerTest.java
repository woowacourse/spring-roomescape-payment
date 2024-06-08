package roomescape.controller.reservation;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.IntegrationTestSupport;
import roomescape.controller.reservation.dto.CreateReservationRequest;
import roomescape.controller.reservation.dto.ReservationResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AdminReservationControllerTest extends IntegrationTestSupport {

    @Test
    @DisplayName("ADMIN이 예약 관리 페이지로 이동")
    void moveToAdminReservationPage() {
        RestAssured.given().log().all()
                .cookie("token", ADMIN_TOKEN)
                .when().get("/admin/reservation")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("ADMIN이 시간 관리 페이지로 이동")
    void moveToAdminTimePage() {
        RestAssured.given().log().all()
                .cookie("token", ADMIN_TOKEN)
                .when().get("/admin/time")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("ADMIN이 테마 관리 페이지로 이동")
    void moveToAdminThemePage() {
        RestAssured.given().log().all()
                .cookie("token", ADMIN_TOKEN)
                .when().get("/admin/theme")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("ADMIN이 예약 생성")
    void createReservationAdmin() {
        final CreateReservationRequest request = new CreateReservationRequest(1L,
                1L, LocalDate.now().plusDays(1), 1L, "tgen_20240529194618t4hG2", "MC4wMzc1NDM4Njg4NTE1", 1000L);

        RestAssured.given().log().all()
                .cookie("token", ADMIN_TOKEN)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("ADMIN이 예약 대기 목록 조회")
    void getWaitReservation() {
        RestAssured.given().log().all()
                .cookie("token", ADMIN_TOKEN)
                .contentType(ContentType.JSON)
                .when().get("/admin/waitings")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @ParameterizedTest
    @DisplayName("ADMIN이 예약 조건 조회")
    @MethodSource("searchReservationsParameterProvider")
    void searchReservations(final Map<String, Object> param, final List<Long> expected) {
        final List<ReservationResponse> result = RestAssured.given().log().all()
                .cookie("token", ADMIN_TOKEN)
                .contentType(ContentType.JSON)
                .params(param)
                .when().get("/admin/search")
                .then().log().all()
                .statusCode(200)
                .extract().jsonPath().getList("$", ReservationResponse.class);

        final List<Long> ids = result.stream()
                .map(ReservationResponse::id)
                .toList();

        assertThat(ids).isEqualTo(expected);
    }

    static Stream<Arguments> searchReservationsParameterProvider() {
        final LocalDate date = LocalDate.now();

        return Stream.of(
                Arguments.of(Map.of("themeId", 2L, "memberId", 2L, "dateFrom", String.valueOf(date.minusDays(8)),
                                "dateTo", String.valueOf(date.minusDays(1))),
                        List.of(2L, 4L)),
                Arguments.of(Map.of("themeId", 3L, "memberId", 3L, "dateFrom", String.valueOf(date.minusDays(5)),
                                "dateTo", String.valueOf(date.plusDays(5))),
                        List.of(6L, 8L)),
                Arguments.of(Map.of("themeId", 3L, "memberId", 3L, "dateFrom", String.valueOf(date.minusDays(7)),
                                "dateTo", String.valueOf(date.plusDays(4))),
                        List.of(5L, 6L, 8L)),
                Arguments.of(Map.of("themeId", 1L, "memberId", 1L, "dateFrom", String.valueOf(date.minusDays(14)),
                                "dateTo", String.valueOf(date.minusDays(7))),
                        List.of())
        );
    }
}
