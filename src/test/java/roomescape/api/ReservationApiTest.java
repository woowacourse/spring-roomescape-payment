package roomescape.api;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static roomescape.LoginTestSetting.getCookieByLogin;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import roomescape.dto.login.LoginRequest;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.dto.reservation.ReservationRequest;
import roomescape.dto.reservation.UserReservationPaymentRequest;
import roomescape.infrastructure.payment.toss.TossPaymentClient;

@Sql("/member-theme-time-test-data.sql")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ReservationApiTest {

    @MockBean
    TossPaymentClient tossPaymentClient;

    @LocalServerPort
    int port;

    Cookie adminToken;
    Cookie userToken;
    Cookie otherUserToken;

    @BeforeEach
    void insert() {
        RestAssured.port = port;

        adminToken = RestAssured.given().log().all()
                .port(port)
                .body(new LoginRequest("admin@email.com", "123456"))
                .contentType(ContentType.JSON)
                .when().post("/login")
                .getDetailedCookie("token");

        userToken = RestAssured.given().log().all()
                .port(port)
                .body(new LoginRequest("ted@email.com", "123456"))
                .contentType(ContentType.JSON)
                .when().post("/login")
                .getDetailedCookie("token");

        otherUserToken = RestAssured.given().log().all()
                .port(port)
                .body(new LoginRequest("atom@email.com", "123456"))
                .contentType(ContentType.JSON)
                .when().post("/login")
                .getDetailedCookie("token");
    }

    @Test
    void 사용자_예약_추가() {
        UserReservationPaymentRequest userReservationPaymentRequest = new UserReservationPaymentRequest(
                LocalDate.now().plusDays(1), 1L, 1L, 2L,
                "paymetKey", "orderId", BigDecimal.valueOf(1000), "paymentType");

        PaymentRequest paymentRequest = PaymentRequest.from(userReservationPaymentRequest);
        PaymentResponse paymentResponse = new PaymentResponse(paymentRequest.paymentKey(), paymentRequest.orderId(),
                paymentRequest.amount());
        Mockito.when(tossPaymentClient.confirm(paymentRequest)).thenReturn(paymentResponse);

        RestAssured.given().log().all()
                .port(port)
                .contentType(ContentType.JSON)
                .cookie(userToken)
                .body(userReservationPaymentRequest)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .body("id", equalTo(1));
    }

    @Test
    void 관리자_예약_추가() {

        Cookie cookieByAdminLogin = getCookieByLogin(port, "admin@email.com", "123456");
        ReservationRequest reservationRequest = createReservationRequest(2L, 1L);

        RestAssured.given().log().all()
                .port(port)
                .contentType(ContentType.JSON)
                .cookie(cookieByAdminLogin)
                .body(reservationRequest)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201)
                .header("Location", "/reservations/1")
                .body("id", equalTo(1))
                .body("date", equalTo(reservationRequest.date().toString()))
                .body("time.id", equalTo(reservationRequest.timeId().intValue()))
                .body("theme.id", equalTo(reservationRequest.themeId().intValue()))
                .body("member.id", equalTo(reservationRequest.memberId().intValue()));
    }

    @Test
    void 예약_단일_조회() {
        ReservationRequest reservationRequest = createReservationRequest(2L, 1L);
        addReservationByAdmin(reservationRequest);

        RestAssured.given().log().all()
                .port(port)
                .when().get("/reservations/1")
                .then().log().all()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("date", equalTo(reservationRequest.date().toString()))
                .body("time.id", equalTo((reservationRequest.timeId().intValue())))
                .body("theme.id", equalTo(reservationRequest.themeId().intValue()))
                .body("member.id", equalTo(reservationRequest.memberId().intValue()));
    }

    @Test
    void 예약_전체_조회() {
        ReservationRequest reservationRequest = createReservationRequest(2L, 1L);
        addReservationByAdmin(reservationRequest);

        RestAssured.given().log().all()
                .port(port)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    void 사용자_예약_전체_조회() {
        UserReservationPaymentRequest userReservationPaymentRequest = createUserReservationPaymentRequest(2L, 2L);
        addReservationByUser(userReservationPaymentRequest);
        UserReservationPaymentRequest otherUserReservationPaymentRequest = createUserReservationPaymentRequest(3L, 3L);
        addReservationByOtherUser(otherUserReservationPaymentRequest);

        RestAssured.given().log().all()
                .port(port)
                .cookie(userToken)
                .when().get("/reservations-mine")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Sql("/reservation-filter-api-test-data.sql")
    @Test
    void 예약_조회시_조회필터_적용하여_조회() {
        RestAssured.given().log().all()
                .port(port)
                .when().get("/reservations/search?memberId=1")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2))
                .body("member.id", everyItem(is(1)));
    }

    @Test
    void 예약_삭제() {
        ReservationRequest reservationRequest = createReservationRequest(2L, 1L);
        addReservationByAdmin(reservationRequest);

        RestAssured.given().log().all()
                .port(port)
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(204);
    }

    private UserReservationPaymentRequest createUserReservationPaymentRequest() {
        return new UserReservationPaymentRequest(LocalDate.now().plusDays(1), 1L, 1L, 2L, "paymetKey", "orderId",
                BigDecimal.valueOf(1000), "paymentType");
    }


    private ReservationRequest createReservationRequest(Long memberId, Long timeId) {
        return new ReservationRequest(LocalDate.now().plusDays(1), timeId, 1L, memberId);
    }

    private UserReservationPaymentRequest createUserReservationPaymentRequest(Long memberId, Long timeId) {
        return new UserReservationPaymentRequest(LocalDate.now().plusDays(1), timeId, 1L, memberId, "paymetKey",
                "orderId", BigDecimal.valueOf(1000), "paymentType");
    }

    private void addReservationByAdmin(ReservationRequest reservationRequest) {
        Cookie cookieByAdminLogin = getCookieByLogin(port, "admin@email.com", "123456");
        RestAssured.given().log().all()
                .port(port)
                .cookie(cookieByAdminLogin)
                .contentType(ContentType.JSON)
                .body(reservationRequest)
                .when().post("/admin/reservations");
    }

    private void addReservationByUser(UserReservationPaymentRequest userReservationPaymentRequest) {
        PaymentRequest paymentRequest = PaymentRequest.from(userReservationPaymentRequest);
        PaymentResponse paymentResponse = new PaymentResponse(paymentRequest.paymentKey(), paymentRequest.orderId(),
                paymentRequest.amount());
        Mockito.when(tossPaymentClient.confirm(paymentRequest)).thenReturn(paymentResponse);

        RestAssured.given().log().all()
                .port(port)
                .cookie(userToken)
                .contentType(ContentType.JSON)
                .body(userReservationPaymentRequest)
                .when().post("/reservations");
    }

    private void addReservationByOtherUser(UserReservationPaymentRequest userReservationPaymentRequest) {
        PaymentRequest paymentRequest = PaymentRequest.from(userReservationPaymentRequest);
        PaymentResponse paymentResponse = new PaymentResponse(paymentRequest.paymentKey(), paymentRequest.orderId(),
                paymentRequest.amount());
        Mockito.when(tossPaymentClient.confirm(paymentRequest)).thenReturn(paymentResponse);

        RestAssured.given().log().all()
                .port(port)
                .cookie(adminToken)
                .contentType(ContentType.JSON)
                .body(userReservationPaymentRequest)
                .when().post("/reservations");
    }
}

