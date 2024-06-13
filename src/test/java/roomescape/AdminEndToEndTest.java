package roomescape;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.controller.reservation.dto.CreateReservationRequest;
import roomescape.domain.Reservation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static roomescape.controller.doc.DocumentFilter.ADMIN_SAVE_RESERVATION;
import static roomescape.controller.doc.DocumentFilter.DELETE_RESERVATION;
import static roomescape.controller.doc.DocumentFilter.DELETE_RESERVATION_FAIL;
import static roomescape.controller.doc.DocumentFilter.DELETE_THEME;
import static roomescape.controller.doc.DocumentFilter.DELETE_THEME_FAIL;
import static roomescape.controller.doc.DocumentFilter.DELETE_TIME;
import static roomescape.controller.doc.DocumentFilter.DELETE_TIME_FAIL;
import static roomescape.controller.doc.DocumentFilter.GET_RESERVATIONS;
import static roomescape.controller.doc.DocumentFilter.GET_THEMES;
import static roomescape.controller.doc.DocumentFilter.GET_TIMES;
import static roomescape.controller.doc.DocumentFilter.SAVE_THEME;
import static roomescape.controller.doc.DocumentFilter.SAVE_TIME;

class AdminEndToEndTest extends IntegrationTestSupport {

    @Test
    @DisplayName("시간 저장 및 삭제")
    void saveAndDeleteTime() {
        final Map<String, String> params = Map.of("startAt", "10:00");

        RestAssured.given(specification).log().all()
                .filter(SAVE_TIME.getValue())
                .cookie("token", ADMIN_TOKEN)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/times")
                .then().log().all()
                .statusCode(201);

        RestAssured.given(specification).log().all()
                .filter(GET_TIMES.getValue())
                .cookie("token", ADMIN_TOKEN)
                .when().get("/times")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(6));

        RestAssured.given(specification).log().all()
                .filter(DELETE_TIME.getValue())
                .cookie("token", ADMIN_TOKEN)
                .when().delete("/times/6")
                .then().log().all()
                .statusCode(204);

        RestAssured.given(specification).log().all()
                .filter(DELETE_TIME_FAIL.getValue())
                .cookie("token", ADMIN_TOKEN)
                .when().delete("/times/6")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("테마 저장 및 삭제")
    void saveAndDeleteTheme() {
        final Map<String, String> params = Map.of("name", "v1", "description", "blah", "thumbnail", "dkdk");

        RestAssured.given(specification).log().all()
                .filter(SAVE_THEME.getValue())
                .cookie("token", ADMIN_TOKEN)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/themes")
                .then().log().all()
                .statusCode(201);

        RestAssured.given(specification).log().all()
                .filter(GET_THEMES.getValue())
                .cookie("token", ADMIN_TOKEN)
                .when().get("/themes")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(5));

        RestAssured.given(specification).log().all()
                .filter(DELETE_THEME.getValue())
                .cookie("token", ADMIN_TOKEN)
                .when().delete("/themes/5")
                .then().log().all()
                .statusCode(204);

        RestAssured.given(specification).log().all()
                .filter(DELETE_THEME_FAIL.getValue())
                .cookie("token", ADMIN_TOKEN)
                .when().delete("/themes/5")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("어드민이 예약 저장 및 삭제")
    void saveAndDeleteReservation() {
        doNothing().when(paymentService).savePayment(any(CreateReservationRequest.class), any(Reservation.class));

        final Map<String, String> params = Map.of("date", LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_DATE),
                "timeId", "1", "themeId", "1", "memberId", "1");

        RestAssured.given(specification).log().all()
                .filter(ADMIN_SAVE_RESERVATION.getValue())
                .cookie("token", ADMIN_TOKEN)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201);

        RestAssured.given(specification).log().all()
                .filter(GET_RESERVATIONS.getValue())
                .cookie("token", ADMIN_TOKEN)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(8));

        RestAssured.given(specification).log().all()
                .filter(DELETE_RESERVATION.getValue())
                .cookie("token", ADMIN_TOKEN)
                .when().delete("/admin/reservations/9")
                .then().log().all()
                .statusCode(204);

        RestAssured.given(specification).log().all()
                .filter(DELETE_RESERVATION_FAIL.getValue())
                .cookie("token", ADMIN_TOKEN)
                .when().delete("/admin/reservations/9")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("로그인 페이지를 반환")
    void showLoginPage() {
        RestAssured.given().log().all()
                .when().get("/login")
                .then().log().all()
                .statusCode(200);
    }
}
