package roomescape.controller.reservation;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.IntegrationTestSupport;
import roomescape.controller.reservation.dto.MemberResponse;
import roomescape.controller.reservation.dto.ReservationResponse;
import roomescape.controller.theme.dto.ReservationThemeResponse;
import roomescape.controller.time.dto.AvailabilityTimeResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationControllerTest extends IntegrationTestSupport {

    @Autowired
    ReservationController reservationController;

    @ParameterizedTest
    @MethodSource("invalidRequestParameterProvider")
    @DisplayName("유효하지 않은 요청인 경우 400을 반환한다.")
    void invalidRequest(final String date, final String timeId, final String themeId) {
        final Map<String, String> params = Map.of("date", date, "timeId", timeId, "themeId", themeId);

        RestAssured.given().log().all()
                .cookie("token", USER_TOKEN)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("나의 예약 조회")
    void getReservationMine() {
        RestAssured.given().log().all()
                .cookie("token", USER_TOKEN)
                .contentType(ContentType.JSON)
                .when().get("/reservations/mine")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(3));
    }

    @Test
    @DisplayName("예약 대기 삭제")
    void deleteWaitReservation() {
        RestAssured.given().log().all()
                .cookie("token", USER_TOKEN)
                .contentType(ContentType.JSON)
                .when().delete("/reservations/wait/8")
                .then().log().all()
                .statusCode(204);
    }

    static Stream<Arguments> invalidRequestParameterProvider() {
        final String date = LocalDate.now().plusDays(5).format(DateTimeFormatter.ISO_DATE);
        final String timeId = "1";
        final String themeId = "1";

        return Stream.of(
                Arguments.of(date, "dk", themeId),
                Arguments.of(date, timeId, "al"),
                Arguments.of("2023", timeId, themeId)
        );
    }
}
