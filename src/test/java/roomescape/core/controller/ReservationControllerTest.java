package roomescape.core.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import roomescape.core.domain.Payment;
import roomescape.core.dto.payment.PaymentConfirmRequest;
import roomescape.core.dto.payment.PaymentConfirmResponse;
import roomescape.core.dto.reservation.ReservationPaymentRequest;
import roomescape.core.dto.waiting.MemberWaitingRequest;
import roomescape.infrastructure.PaymentApprover;
import roomescape.utils.AccessTokenGenerator;
import roomescape.utils.DatabaseCleaner;
import roomescape.utils.TestFixture;

@AcceptanceTest
class ReservationControllerTest {
    private static final String TOMORROW = TestFixture.getTomorrowDate();
    private static final String DAY_AFTER_TOMORROW = TestFixture.getDayAfterTomorrowDate();
    private static final String RESERVATION_IS_NOT_YOURS_EXCEPTION_MESSAGE = "본인의 예약만 취소할 수 있습니다.";

    @LocalServerPort
    private int port;

    @SpyBean
    private PaymentApprover paymentApprover;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private TestFixture testFixture;

    private String accessToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        databaseCleaner.executeTruncate();
        testFixture.initTestData();

        accessToken = AccessTokenGenerator.adminTokenGenerate();
    }

    @Test
    @DisplayName("돈을 지불하며 예약을 생성한다.")
    void createAndPay() {
        ReservationPaymentRequest request
                = new ReservationPaymentRequest(TOMORROW, 1L, 1L, "1", "1", 1);

        Mockito.doReturn(new PaymentConfirmResponse(new Payment("1", "1", 1L)))
                .when(paymentApprover)
                .confirmPayment(any(PaymentConfirmRequest.class));

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("토스에서 발생한 400번대 예외를 400번과 메시지로 응답한다.")
    void throw4xxErrorMessageAs400AndDetail() {
        ReservationPaymentRequest request
                = new ReservationPaymentRequest(TOMORROW, 1L, 1L, "1", "1", 1);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .body("detail", is("결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다."));
    }


    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "abc"})
    @DisplayName("예약 생성 시, date의 형식이 올바르지 않으면 예외가 발생한다.")
    void validateReservationWithDateFormat(final String date) {
        ReservationPaymentRequest request = new ReservationPaymentRequest(date, 1L, 1L, "", "", 1);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("예약 생성 시, timeId가 null이면 예외가 발생한다.")
    void validateReservationWithNullTimeId() {
        ReservationPaymentRequest request
                = new ReservationPaymentRequest(TOMORROW, null, 1L, "", "", 1);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("예약 생성 시, themeId가 null이면 예외가 발생한다.")
    void validateReservationWithNullThemeId() {
        ReservationPaymentRequest request
                = new ReservationPaymentRequest(TOMORROW, 1L, null, "", "", 1);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("모든 예약 내역을 조회한다.")
    void findAllReservations() {
        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    @DisplayName("예약을 삭제한다.")
    void validateReservationDelete() {
        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("조건에 따라 예약을 조회한다.")
    void findReservationsByCondition() {
        testFixture.persistReservationWithDateAndTimeAndTheme(TOMORROW, 1L, 1L);
        testFixture.persistReservationWithDateAndTimeAndTheme(DAY_AFTER_TOMORROW, 1L, 1L);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .queryParams(
                        "memberId", 1L,
                        "themeId", 1L,
                        "dateFrom", TOMORROW,
                        "dateTo", TOMORROW
                )
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .queryParams(
                        "memberId", 1L,
                        "themeId", 1L,
                        "dateFrom", TOMORROW,
                        "dateTo", DAY_AFTER_TOMORROW
                )
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));
    }

    @Test
    @DisplayName("토큰이 유효하지 않을 경우 예외가 발생한다.")
    void validateToken() {
        ReservationPaymentRequest request
                = new ReservationPaymentRequest(TOMORROW, 1L, 1L, "", "", 1);

        RestAssured.given().log().all()
                .cookies("token", "invalid-token")
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    @DisplayName("현재 로그인된 회원의 예약 대기를 포함한 예약 목록을 조회한다.")
    void findLoginMemberReservation() {
        MemberWaitingRequest waitingRequest = new MemberWaitingRequest(TOMORROW, 1L, 1L);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(waitingRequest)
                .when().post("/waitings")
                .then().log().all()
                .statusCode(201);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .when().get("/reservations/mine")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));
    }

    @Test
    @DisplayName("내 예약이 아닌 다른 회원의 예약을 삭제하면 예외가 발생한다.")
    void validateDeleteOtherMemberReservation() {
        testFixture.persistReservationWithDateAndTimeAndTheme(TOMORROW, 1L, 1L);

        RestAssured.given().log().all()
                .cookies("token", AccessTokenGenerator.memberTokenGenerate())
                .when().delete("/reservations/2")
                .then().log().all()
                .statusCode(400)
                .body("detail", is(RESERVATION_IS_NOT_YOURS_EXCEPTION_MESSAGE));
    }
}
