package roomescape.reservation.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.payment.service.TossPaymentService;
import roomescape.payment.service.dto.ConfirmPaymentRequest;
import roomescape.payment.service.dto.ConfirmPaymentResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class ReservationControllerTest {

    @MockitoBean
    private TossPaymentService mockTossPaymentService = Mockito.mock(TossPaymentService.class);

    @DisplayName("어드민 페이지로 접근할 수 있다.")
    @Test
    void test1() {
        String tokenValue = getAdminLoginTokenValue();

        RestAssured.given().log().all()
                .cookie("token", tokenValue)
                .when().get("/admin")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("어드민이 예약 관리 페이지에 접근한다.")
    @Test
    void test2() {
        String tokenValue = getAdminLoginTokenValue();

        RestAssured.given().log().all()
                .cookie("token", tokenValue)
                .when().get("/admin/reservation")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("모든 예약 정보를 반환한다.")
    @Test
    void test3() {
        ConfirmPaymentRequest paymentRequest = new ConfirmPaymentRequest("paymentKey", "1234", 1000);
        ConfirmPaymentResponse paymentResponse = new ConfirmPaymentResponse(1000, "paymentKey", null);
        Mockito.when(mockTossPaymentService.postConfirmPayment(paymentRequest)).thenReturn(paymentResponse);


        addReservationTime("10:00");
        addTheme();
        String tokenValue = getAdminLoginTokenValue();

        RestAssured.given()
                .cookie("token", tokenValue)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "date", LocalDate.now().plusDays(1L),
                        "timeId", 1,
                        "themeId", 1,
                        "paymentKey", paymentRequest.paymentKey(),
                        "orderId", paymentRequest.orderId(),
                        "amount", paymentRequest.amount()
                ))
                .when().post("/reservations")
                .then();

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));

    }

    @DisplayName("예약 정보를 추가한다.")
    @Test
    void test4() {
        ConfirmPaymentRequest paymentRequest = new ConfirmPaymentRequest("paymentKey", "1234", 1000);
        ConfirmPaymentResponse paymentResponse = new ConfirmPaymentResponse(1000, "paymentKey", null);
        Mockito.when(mockTossPaymentService.postConfirmPayment(paymentRequest)).thenReturn(paymentResponse);

        int timeId = addReservationTime("10:00");
        int themeId = addTheme();
        String tokenValue = getAdminLoginTokenValue();
        Map<String, Object> reservationParams = Map.of(
                "date", LocalDate.now().plusDays(1L),
                "timeId", timeId,
                "themeId", themeId,
                "paymentKey", paymentRequest.paymentKey(),
                "orderId", paymentRequest.orderId(),
                "amount", paymentRequest.amount()
        );

        RestAssured.given().log().all()
                .cookie("token", tokenValue)
                .contentType(ContentType.JSON)
                .body(reservationParams)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("존재하지 않는 예약 시간 ID 를 추가하면 예외를 반환한다.")
    @Test
    void test5() {
        ConfirmPaymentRequest paymentRequest = new ConfirmPaymentRequest("paymentKey", "1234", 1000);
        ConfirmPaymentResponse paymentResponse = new ConfirmPaymentResponse(1000, "paymentKey", null);
        Mockito.when(mockTossPaymentService.postConfirmPayment(paymentRequest)).thenReturn(paymentResponse);

        String tokenValue = getAdminLoginTokenValue();
        int themeId = addTheme();
        Map<String, Object> reservationParams = Map.of(
                "date", LocalDate.now().plusDays(1L),
                "timeId", 0,
                "themeId", themeId,
                "paymentKey", paymentRequest.paymentKey(),
                "orderId", paymentRequest.orderId(),
                "amount", paymentRequest.amount()
        );

        RestAssured.given().log().all()
                .cookie("token", tokenValue)
                .contentType(ContentType.JSON)
                .body(reservationParams)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(404);
    }

    @DisplayName("존재하지 않는 테마 ID 를 추가하면 예외를 반환한다.")
    @Test
    void notExistThemeId() {
        ConfirmPaymentRequest paymentRequest = new ConfirmPaymentRequest("paymentKey", "1234", 1000);
        ConfirmPaymentResponse paymentResponse = new ConfirmPaymentResponse(1000, "paymentKey", null);
        Mockito.when(mockTossPaymentService.postConfirmPayment(paymentRequest)).thenReturn(paymentResponse);

        String tokenValue = getAdminLoginTokenValue();
        int timeId = addReservationTime("10:00");
        Map<String, Object> reservationParams = Map.of(
                "date", LocalDate.now().plusDays(1L),
                "timeId", timeId,
                "themeId", 0,
                "paymentKey", paymentRequest.paymentKey(),
                "orderId", paymentRequest.orderId(),
                "amount", paymentRequest.amount()
        );

        RestAssured.given().log().all()
                .cookie("token", tokenValue)
                .contentType(ContentType.JSON)
                .body(reservationParams)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(404);
    }

    @DisplayName("예약을 삭제한다.")
    @Test
    void test6() {
        ConfirmPaymentRequest paymentRequest = new ConfirmPaymentRequest("paymentKey", "1234", 1000);
        ConfirmPaymentResponse paymentResponse = new ConfirmPaymentResponse(1000, "paymentKey", null);
        Mockito.when(mockTossPaymentService.postConfirmPayment(paymentRequest)).thenReturn(paymentResponse);

        int timeId = addReservationTime("10:00");
        int themeId = addTheme();
        String tokenValue = getAdminLoginTokenValue();
        Map<String, Object> reservationParams = Map.of(
                "date", LocalDate.now().plusDays(1L),
                "timeId", timeId,
                "themeId", themeId,
                "paymentKey", paymentRequest.paymentKey(),
                "orderId", paymentRequest.orderId(),
                "amount", paymentRequest.amount()
        );

        int reservationId = RestAssured.given()
                .cookie("token", tokenValue)
                .contentType(ContentType.JSON)
                .body(reservationParams)
                .when().post("/reservations")
                .then().extract().path("id");

        RestAssured.given().log().all()
                .cookie("token", tokenValue)
                .when().delete("/reservations/" + reservationId)
                .then().log().all()
                .statusCode(204);
    }

    @DisplayName("존재하지 않는 예약을 삭제할 경우 NOT_FOUND 반환")
    @Test
    void test7() {
        String tokenValue = getAdminLoginTokenValue();

        RestAssured.given().log().all()
                .cookie("token", tokenValue)
                .when().delete("/reservations/0")
                .then().log().all()
                .statusCode(404);
    }

    @DisplayName("예약 가능한 시간을 반환한다")
    @Test
    void test9() {
        ConfirmPaymentRequest paymentRequest = new ConfirmPaymentRequest("paymentKey", "1234", 1000);
        ConfirmPaymentResponse paymentResponse = new ConfirmPaymentResponse(1000, "paymentKey", null);
        Mockito.when(mockTossPaymentService.postConfirmPayment(paymentRequest)).thenReturn(paymentResponse);

        int timeId1 = addReservationTime("10:00");
        int timeId2 = addReservationTime("11:00");
        int themeId = addTheme();
        String tokenValue = getAdminLoginTokenValue();
        LocalDate day = LocalDate.now().plusDays(1L);
        Map<String, Object> reservationParams = Map.of(
                "date", day,
                "timeId", timeId1,
                "themeId", themeId,
                "paymentKey", paymentRequest.paymentKey(),
                "orderId", paymentRequest.orderId(),
                "amount", paymentRequest.amount()
        );

        RestAssured.given()
                .cookie("token", tokenValue)
                .contentType(ContentType.JSON)
                .body(reservationParams)
                .when().post("/reservations")
                .then();

        String date = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(day);
        RestAssured.given().log().all()
                .when().get("/reservations/times?date=" + date + "&themeId=" + themeId)
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2))
                .body("alreadyBooked", containsInAnyOrder(true, false));
    }

    private String getAdminLoginTokenValue() {
        Map<String, String> adminLoginParams = Map.of("email", "admin@woowa.com", "password", "12341234");
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(adminLoginParams)
                .when().post("/login")
                .then()
                .statusCode(200)
                .extract().cookie("token");
    }

    private int addReservationTime(final String timeValue) {
        Map<String, String> timeParams = Map.of("startAt", timeValue);

        return RestAssured.given()
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
