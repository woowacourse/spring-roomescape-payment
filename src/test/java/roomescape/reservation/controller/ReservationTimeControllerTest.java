package roomescape.reservation.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.service.PaymentService;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class ReservationTimeControllerTest {

    @MockitoBean
    private PaymentService paymentService;

    @DisplayName("예약 시간을 추가한다.")
    @Test
    void postTimes() {
        Map<String, String> params = new HashMap<>();
        params.put("startAt", "10:00");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/times")
                .then().log().all()
                .statusCode(201);
    }

    @DisplayName("예약 시간에 초(seconds)가 포함되어 있으면 추가할 수 없다.")
    @Test
    void postTimesWithSeconds() {
        Map<String, String> params = new HashMap<>();
        params.put("startAt", "10:00:20");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/times")
                .then().log().all()
                .statusCode(400);
    }

    @DisplayName("모든 예약 시간을 가져온다.")
    @Test
    void getTimes() {
        addReservationTime("10:00");

        RestAssured.given().log().all()
                .when().get("/times")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @DisplayName("해당 예약 시간을 삭제한다.")
    @Test
    void deleteTimes() {
        int timeId = addReservationTime("10:00");

        RestAssured.given().log().all()
                .when().delete("/times/" + timeId)
                .then().log().all()
                .statusCode(204);
    }

    @DisplayName("존재하지 않는 예약 시간은 삭제할 수 없다.")
    @Test
    void deleteTimesWithNonExistsTimeId() {
        RestAssured.given().log().all()
                .when().delete("/times/0")
                .then().log().all()
                .statusCode(404);
    }

    @DisplayName("사용중인 예약 시간은 삭제할 수 없다.")
    @Test
    void deleteTimesWhenUsing() {
        int conflictStatusCode = 409;
        int timeId = addReservationTime("10:00");
        int themeId = addTheme();
        addReservation(timeId, themeId);

        RestAssured.given().log().all()
                .when().delete("/times/" + timeId)
                .then().log().all()
                .statusCode(conflictStatusCode);
    }

    private int addReservation(final int timeId, final int themeId) {
        String tokenValue = getAdminLoginTokenValue();

        Map<String, Object> reservationParams = Map.of(
                "date", LocalDate.now().plusDays(1L),
                "timeId", timeId,
                "themeId", themeId,
                "paymentKey", "paymentKey",
                "orderId", "orderId",
                "amount", 1_000L
        );

        doNothing().when(paymentService)
                .confirm(any(PaymentRequest.class));

        return RestAssured.given().log().all()
                .cookie("token", tokenValue)
                .contentType(ContentType.JSON)
                .body(reservationParams)
                .when().post("/reservations")
                .then().extract().path("id");
    }

    private String getAdminLoginTokenValue() {
        Map<String, String> adminLoginParams = Map.of("email", "admin@woowa.com", "password", "12341234");
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(adminLoginParams)
                .when().post("/login")
                .then()
                .extract().cookie("token");
    }

    private int addReservationTime(final String timeValue) {
        Map<String, String> timeParams = Map.of("startAt", timeValue);

        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(timeParams)
                .when().post("/times")
                .then().extract().path("id");
    }

    private int addTheme() {
        Map<String, String> themeParams = Map.of(
                "name", "테마1", "description", "테마1", "thumbnail", "www.m.com"
        );
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(themeParams)
                .when().post("/themes")
                .then().extract().path("id");
    }
}
