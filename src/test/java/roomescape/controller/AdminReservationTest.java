package roomescape.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static roomescape.fixture.TestFixture.RESERVATION_COUNT;
import static roomescape.fixture.TestFixture.TOKEN;
import static roomescape.fixture.TestFixture.WAITING_COUNT;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.Role;
import roomescape.infrastructure.TokenGenerator;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {"spring.config.location=classpath:/application-test.yml"})
class AdminReservationTest {

    private static final String EMAIL = "test@email.com";

    @Autowired
    private TokenGenerator tokenGenerator;
    @LocalServerPort
    private int port;
    private String accessToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        accessToken = tokenGenerator.createToken(EMAIL, Role.ADMIN.name());
    }

    @DisplayName("reservation 페이지 조회 요청이 올바르게 연결된다.")
    @Test
    void given_when_GetReservations_then_statusCodeIsOkay() {
        RestAssured.given().log().all()
                .cookies(TOKEN, accessToken)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(RESERVATION_COUNT));
    }

    @DisplayName("reservation 페이지에 새로운 예약 정보를 조회, 삭제할 수 있다.")
    @Test
    void given_when_saveAndDeleteReservations_then_statusCodeIsOkay() {
        RestAssured.given().log().all()
                .cookies(TOKEN, accessToken)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(RESERVATION_COUNT));

        RestAssured.given().log().all()
                .cookies(TOKEN, accessToken)
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(204);
    }

    @DisplayName("선예약이 취소될 경우 가장 첫 번째 예약 대기가 예약 단계로 넘어간다.")
    @Test
    void given_when_cancelReservation_then_firstWaitingBecomesNewReservation() {
        RestAssured.given().log().all()
                .cookies(TOKEN, accessToken)
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(204);

        RestAssured.given().log().all()
                .cookies(TOKEN, accessToken)
                .when().get("/reservations")
                .then().log().all()
                .body("size()", is(RESERVATION_COUNT));

        RestAssured.given().log().all()
                .cookies(TOKEN, accessToken)
                .when().get("admin/waitings")
                .then().log().all()
                .body("size()", is(WAITING_COUNT - 1));
    }

    @DisplayName("등록되지 않은 시간으로 예약하는 경우 400 오류를 반환한다.")
    @Test
    void given_when_saveNotExistTimeId_then_statusCodeIsBadRequest() {
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("name", "브라운");
        reservation.put("date", "2099-01-01");
        reservation.put("timeId", 500);
        reservation.put("themeId", 1);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(TOKEN, accessToken)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @DisplayName("부적절한 날짜로 예약하는 경우 400 오류를 반환한다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" ", "", "2011-02-09"})
    void given_when_saveInvalidDate_then_statusCodeIsBadRequest(String invalidDate) {
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("name", "브라운");
        reservation.put("date", invalidDate);
        reservation.put("timeId", 1);
        reservation.put("themeId", 1);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(TOKEN, accessToken)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .body(containsString("[ERROR] 올바르지 않은 예약 날짜입니다."));
    }

    @DisplayName("부적절한 시간으로 예약하는 경우 400 오류를 반환한다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" ", ""})
    void given_when_saveInvalidTimeId_then_statusCodeIsBadRequest(String invalidTimeId) {
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("name", "브라운");
        reservation.put("date", "2999-12-31");
        reservation.put("timeId", invalidTimeId);
        reservation.put("themeId", 1);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(TOKEN, accessToken)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .body(containsString("[ERROR] 올바르지 않은 예약 시간입니다."));
    }

    @DisplayName("Null 값의 paymentKey로 예약하려 할 경우 400 오류를 반환한다.")
    @Test
    void given_when_saveWithoutPaymentKey_then_statusCodeIsBadRequest() {
        Map<String, Object> reservation = Map.of(
                "name", "브라운",
                "date", "2024-09-01",
                "timeId", 1,
                "themeId", 1,
                "orderId", "orderId",
                "amount", 1L
        );

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(TOKEN, accessToken)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .body(containsString("[ERROR] 올바르지 않은 paymentKey입니다."));
    }

    @DisplayName("Null 값의 orderId로 예약하려 할 경우 400 오류를 반환한다.")
    @Test
    void given_when_saveWithoutOrderId_then_statusCodeIsBadRequest() {
        Map<String, Object> reservation = Map.of(
                "name", "브라운",
                "date", "2024-09-01",
                "timeId", 1,
                "themeId", 1,
                "paymentKey", "paymentKey",
                "amount", 1L
        );

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(TOKEN, accessToken)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .body(containsString("[ERROR] 올바르지 않은 orderId입니다."));
    }

    @DisplayName("Null 값의 amount로 예약하려 할 경우 400 오류를 반환한다.")
    @Test
    void given_when_saveWithoutAmount_then_statusCodeIsBadRequest() {
        Map<String, Object> reservation = Map.of(
                "name", "브라운",
                "date", "2024-09-01",
                "timeId", 1,
                "themeId", 1,
                "paymentKey", "paymentKey",
                "orderId", "orderId"
        );

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(TOKEN, accessToken)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .body(containsString("[ERROR] 올바르지 않은 amount입니다."));
    }

    @DisplayName("Null 값의 amount로 예약하려 할 경우 400 오류를 반환한다.")
    @Test
    void given_when_saveWithInvalidAmount_then_statusCodeIsBadRequest() {
        Map<String, Object> reservation = Map.of(
                "name", "브라운",
                "date", "2024-09-01",
                "timeId", 1,
                "themeId", 1,
                "paymentKey", "paymentKey",
                "orderId", "orderId",
                "amount", -1L
        );

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(TOKEN, accessToken)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .body(containsString("[ERROR] 올바르지 않은 amount입니다."));
    }

    @DisplayName("지나간 날짜와 시간으로 예약할 경우 400 오류를 반환한다.")
    @Test
    void given_when_saveWithPastReservation_then_statusCodeIsBadRequest() {
        Map<String, Object> reservation = Map.of(
                "name", "브라운",
                "date", LocalDate.now().toString(),
                "timeId", 1,
                "themeId", 1,
                "paymentKey", "paymentKey",
                "orderId", "orderId",
                "amount", 1L
        );

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(TOKEN, accessToken)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .body(containsString("[ERROR] 지나간 날짜와 시간으로 예약할 수 없습니다"));
    }

    @DisplayName("이미 예약이 된 시간을 등록하려 하면 400 오류를 반환한다.")
    @Test
    void given_when_saveDuplicatedReservation_then_statusCodeIsBadRequest() {
        Map<String, Object> reservation = Map.of(
                "name", "포케",
                "date", "2099-04-30",
                "timeId", 1,
                "themeId", 1,
                "paymentKey", "paymentKey",
                "orderId", "orderId",
                "amount", 1L
        );

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(TOKEN, accessToken)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .body(containsString("[ERROR] 예약이 종료되었습니다"));
    }

    @DisplayName("부적절한 테마로 예약하는 경우 400 오류를 반환한다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" ", ""})
    void given_when_saveInvalidThemeId_then_statusCodeIsBadRequest(String invalidThemeId) {
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("name", "브라운");
        reservation.put("date", "2999-04-01");
        reservation.put("timeId", 1);
        reservation.put("themeId", invalidThemeId);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(TOKEN, accessToken)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .body(containsString("[ERROR] 올바르지 않은 테마 입니다."));
    }

    @DisplayName("등록되지 않은 테마로 예약하는 경우 400 오류를 반환한다.")
    @Test
    void given_when_saveNotExistThemeId_then_statusCodeIsBadRequest() {
        Map<String, Object> reservation = Map.of(
                "name", "브라운",
                "date", "2099-01-01",
                "timeId", 1,
                "themeId", 99,
                "paymentKey", "paymentKey",
                "orderId", "orderId",
                "amount", 1L
        );

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(TOKEN, accessToken)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .body(containsString("[ERROR] 존재하지 않는 테마 입니다"));
    }
}
