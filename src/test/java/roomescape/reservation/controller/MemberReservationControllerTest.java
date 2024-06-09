package roomescape.reservation.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;
import roomescape.Fixtures;
import roomescape.auth.service.TokenCookieService;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.infrastructure.TossPaymentRestClient;
import roomescape.reservation.domain.entity.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
@Sql(value = {"/recreate_table.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("사용자 예약 컨트롤러")
class MemberReservationControllerTest {

    @LocalServerPort
    private int port;
    private String accessToken;
    @MockBean
    private TossPaymentRestClient restClient;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        Map<String, String> body = new HashMap<>();
        body.put("email", "test@gmail.com");
        body.put("password", "password");

        accessToken = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .header(HttpHeaders.SET_COOKIE)
                .split(";")[0]
                .split(TokenCookieService.COOKIE_TOKEN_KEY + "=")[1];
    }

    @DisplayName("사용자 예약 컨트롤러는 /reservation으로 GET 요청이 들어오면 사용자 예약 페이지를 반환한다.")
    @Test
    void readUserReservation() {
        RestAssured.given().log().all()
                .cookie(TokenCookieService.COOKIE_TOKEN_KEY, accessToken)
                .when().get("/reservation")
                .then().log().all()
                .statusCode(200)
                .body("html.body.div.h2", equalTo("예약 페이지"));
    }

    @DisplayName("사용자 예약 컨트롤러는 예약 생성 시 생성된 값을 반환한다.")
    @Test
    void createMemberReservation() {
        // given
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", LocalDate.MAX.toString());
        reservation.put("timeId", 1);
        reservation.put("themeId", 1);
        reservation.put("status", ReservationStatus.CONFIRMATION);
        reservation.put("paymentKey", "test");
        reservation.put("orderId", "test");
        reservation.put("amount", 21000);

        PaymentRequest paymentRequest = new PaymentRequest("test", "test", BigDecimal.valueOf(21000));
        Mockito.when(restClient.post("/confirm", paymentRequest))
                .thenReturn(Optional.of(Fixtures.paymentResponseFixture));

        // when
        String name = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(TokenCookieService.COOKIE_TOKEN_KEY, accessToken)
                .body(reservation)
                .queryParam("type", "member")
                .when().post("/reservations")
                .then().log().all()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath().get("memberName");

        // then
        assertThat(name).isEqualTo("클로버");
    }

    @DisplayName("사용자 예약 컨트롤러는 로그인하지 않은 사용자가 예약을 생성하는 경우 예외가 발생한다.")
    @Test
    void createMemberReservationWithOutLogin() {
        // given
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", LocalDate.MAX.toString());
        reservation.put("timeId", 1);
        reservation.put("themeId", 1);
        reservation.put("status", ReservationStatus.CONFIRMATION);

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(reservation)
                .queryParam("type", "member")
                .when().post("/reservations")
                .then().log().all()
                .statusCode(401);
    }

    @DisplayName("사용자 예약 컨트롤러는 잘못된 형식의 날짜로 예약 생성 요청 시 400을 응답한다.")
    @ValueSource(strings = {"Hello", "2024-13-20", "2900-12-32"})
    @ParameterizedTest
    void createInvalidDateReservation(String invalidString) {
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", invalidString);
        reservation.put("timeId", 1);
        reservation.put("themeId", 1);

        String detailMessage = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(TokenCookieService.COOKIE_TOKEN_KEY, accessToken)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .extract()
                .jsonPath().get("detail");

        assertThat(detailMessage).isEqualTo("잘못된 형식의 날짜 혹은 시간입니다.");
    }

    @DisplayName("사용자 예약 컨트롤러는 지난 날짜로 예약 생성 요청 시 400을 응답한다.")
    @Test
    void createReservationWithBeforeDate() {
        // given
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", LocalDate.MIN);
        reservation.put("timeId", 1);
        reservation.put("themeId", 1);
        reservation.put("status", ReservationStatus.CONFIRMATION);
        reservation.put("paymentKey", "test");
        reservation.put("orderId", "test");
        reservation.put("amount", 21000);

        PaymentRequest paymentRequest = new PaymentRequest("test", "test", BigDecimal.valueOf(21000));
        Mockito.when(restClient.post("/confirm", paymentRequest))
                .thenReturn(Optional.of(Fixtures.paymentResponseFixture));


        // when
        String detailMessage = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(TokenCookieService.COOKIE_TOKEN_KEY, accessToken)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .extract()
                .jsonPath().get("detail");

        // then
        assertThat(detailMessage).isEqualTo("이미 지난 날짜는 예약 또는 삭제할 수 없습니다.");
    }

    @DisplayName("사용자 예약 컨트롤러는 중복 예약 생성 요청 시 400을 응답한다.")
    @Test
    void createReservationWithDuplicated() {
        // given
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", LocalDate.MAX);
        reservation.put("timeId", 1);
        reservation.put("themeId", 1);
        reservation.put("status", ReservationStatus.CONFIRMATION);
        reservation.put("paymentKey", "test");
        reservation.put("orderId", "test");
        reservation.put("amount", 21000);

        PaymentRequest paymentRequest = new PaymentRequest("test", "test", BigDecimal.valueOf(21000));
        Mockito.when(restClient.post("/confirm", paymentRequest))
                .thenReturn(Optional.of(Fixtures.paymentResponseFixture));

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(TokenCookieService.COOKIE_TOKEN_KEY, accessToken)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(200);

        // when
        String detailMessage = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(TokenCookieService.COOKIE_TOKEN_KEY, accessToken)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .extract()
                .jsonPath().get("detail");

        // then
        assertThat(detailMessage).isEqualTo("이미 예약한 테마입니다.");
    }

    @DisplayName("사용자 예약 컨트롤러는 예약 대기시 200을 반환한다.")
    @Test
    void createWaitingReservation() {
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", "2099-12-31");
        reservation.put("timeId", 1);
        reservation.put("themeId", 1);
        reservation.put("status", ReservationStatus.WAITING);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(TokenCookieService.COOKIE_TOKEN_KEY, accessToken)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(200);
    }


    @DisplayName("사용자 예약 컨트롤러는 존재하지 않는 시간으로 예약 생성을 요청할 경우 404를 응답한다.")
    @Test
    void createReservationWithNonExistsTime() {
        // given
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", LocalDate.MIN);
        reservation.put("timeId", Integer.MAX_VALUE);
        reservation.put("themeId", 1);
        reservation.put("status", ReservationStatus.CONFIRMATION);
        reservation.put("paymentKey", "test");
        reservation.put("orderId", "test");
        reservation.put("amount", 21000);

        PaymentRequest paymentRequest = new PaymentRequest("test", "test", BigDecimal.valueOf(21000));
        Mockito.when(restClient.post("/confirm", paymentRequest))
                .thenReturn(Optional.of(Fixtures.paymentResponseFixture));

        // when
        String detailMessage = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(TokenCookieService.COOKIE_TOKEN_KEY, accessToken)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(404)
                .extract()
                .jsonPath().get("detail");

        // then
        assertThat(detailMessage).isEqualTo("존재하지 않는 예약 시간입니다.");
    }

    @DisplayName("사용자 예약 컨트롤러는 존재하지 않는 테마로 예약 생성을 요청할 경우 404를 응답한다.")
    @Test
    void createReservationWithNonExistsTheme() {
        // given
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", LocalDate.MIN);
        reservation.put("timeId", 1);
        reservation.put("themeId", Integer.MAX_VALUE);
        reservation.put("status", ReservationStatus.CONFIRMATION);
        reservation.put("paymentKey", "test");
        reservation.put("orderId", "test");
        reservation.put("amount", 21000);

        PaymentRequest paymentRequest = new PaymentRequest("test", "test", BigDecimal.valueOf(21000));
        Mockito.when(restClient.post("/confirm", paymentRequest))
                .thenReturn(Optional.of(Fixtures.paymentResponseFixture));

        // when
        String detailMessage = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(TokenCookieService.COOKIE_TOKEN_KEY, accessToken)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(404)
                .extract()
                .jsonPath().get("detail");

        // then
        assertThat(detailMessage).isEqualTo("존재하지 않는 테마입니다.");
    }

    @DisplayName("사용자 예약 컨트롤러는 예약 생성 시 잘못된 형식의 본문이 들어오면 400을 응답한다.")
    @Test
    void createInvalidRequestBody() {
        String invalidBody = "invalidBody";

        String detailMessage = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(invalidBody)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .extract()
                .jsonPath().get("detail");

        assertThat(detailMessage).isEqualTo("요청에 잘못된 형식의 값이 있습니다.");
    }

    @DisplayName("사용자 예약 컨트롤러는 내 예약 조회 요청 시 본인의 예약만 응답한다.")
    @Test
    void readMemberReservations() {
        RestAssured.given().log().all()
                .cookie(TokenCookieService.COOKIE_TOKEN_KEY, accessToken)
                .when().get("/reservations/my")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));
    }

    @DisplayName("사용자 예약 컨트롤러는 예약 대기 삭제 요청시 204를 응답한다.")
    @Test
    void deleteMemberReservation() {
        // given
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", LocalDate.MAX);
        reservation.put("timeId", 1);
        reservation.put("themeId", 1);
        reservation.put("status", ReservationStatus.WAITING);
        reservation.put("paymentKey", "test");
        reservation.put("orderId", "test");
        reservation.put("amount", 21000);

        PaymentRequest paymentRequest = new PaymentRequest("test", "test", BigDecimal.valueOf(21000));
        Mockito.when(restClient.post("/confirm", paymentRequest))
                .thenReturn(Optional.of(Fixtures.paymentResponseFixture));

        Long id = RestAssured.given()
                .contentType(ContentType.JSON)
                .cookie(TokenCookieService.COOKIE_TOKEN_KEY, accessToken)
                .body(reservation)
                .when().post("/reservations")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getLong("id");

        // when & then
        RestAssured.given()
                .cookie(TokenCookieService.COOKIE_TOKEN_KEY, accessToken)
                .when().delete("/reservations/" + id)
                .then()
                .statusCode(204);
    }

    @DisplayName("사용자 예약 컨트롤러는 본인것이 아닌 예약 대기 삭제 요청시 403을 응답한다.")
    @Test
    void deleteMemberReservationNotOwner() {
        // given
        long id = 2L;
        Mockito.when(restClient.post("/paymentKey/cancel", Map.of("cancelReason", "reason")))
                .thenReturn(Optional.of(Fixtures.paymentResponseFixture));

        // when
        String detailMessage = RestAssured.given().log().all()
                .cookie(TokenCookieService.COOKIE_TOKEN_KEY, accessToken)
                .when().delete("/reservations/" + id)
                .then()
                .statusCode(403).log().all()
                .extract()
                .jsonPath().get("detail");

        // then
        assertThat(detailMessage).isEqualTo("본인의 예약 대기만 삭제할 수 있습니다.");
    }

    @DisplayName("사용자 예약 컨트롤러는 결제 대기 상태의 예약의 결제가 성공하면 200을 응답한다.")
    @Test
    void confirmPendingReservation() {
        // given
        long id = 15L;
        Mockito.when(restClient.post("/confirm", Fixtures.paymentRequestFixture))
                .thenReturn(Optional.of(Fixtures.paymentResponseFixture));

        // when & then
        RestAssured.given().log().all()
                .cookie(TokenCookieService.COOKIE_TOKEN_KEY, accessToken)
                .when()
                .contentType(ContentType.JSON)
                .body(Fixtures.paymentRequestFixture)
                .post("/reservations/" + id + "/payments/confirm")
                .then()
                .statusCode(200);
    }

    @DisplayName("사용자 예약 컨트롤러는 결제 대기 상태가 아닌 예약의 결제가 발생할 경우 400을 응답한다.")
    @Test
    void confirmNotPendingReservation() {
        // given
        long id = 13L;
        Mockito.when(restClient.post("/confirm", Fixtures.paymentRequestFixture))
                .thenReturn(Optional.of(Fixtures.paymentResponseFixture));

        // when
        String detailMessage = RestAssured.given().log().all()
                .cookie(TokenCookieService.COOKIE_TOKEN_KEY, accessToken)
                .when()
                .contentType(ContentType.JSON)
                .body(Fixtures.paymentRequestFixture)
                .post("/reservations/" + id + "/payments/confirm")
                .then()
                .statusCode(400)
                .extract()
                .jsonPath().get("detail");

        // then
        assertThat(detailMessage).isEqualTo("해당 예약은 결제 대기 상태가 아닙니다.");
    }
}
