package roomescape.api;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static roomescape.TestFixture.ADMIN_EMAIL;
import static roomescape.TestFixture.ADMIN_PASSWORD;
import static roomescape.TestFixture.USER_EMAIL;
import static roomescape.TestFixture.USER_ID;
import static roomescape.TestFixture.USER_PASSWORD;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.dto.reservation.ReservationRequest;
import roomescape.dto.reservation.UserReservationRequest;
import roomescape.infrastructure.auth.JwtProvider;
import roomescape.infrastructure.tosspayments.TossPaymentsClient;

class ReservationApiTest extends ApiBaseTest {

    @LocalServerPort
    int port;

    @Autowired
    JwtProvider jwtProvider;

    @MockBean
    TossPaymentsClient tossPaymentsClient;

    @Test
    void 관리자_예약_추가() {
        Cookie cookieByAdminLogin = getCookieByLogin(port, ADMIN_EMAIL, ADMIN_PASSWORD);
        ReservationRequest reservationRequest = createReservationRequest();

        RestAssured
                .given().log().all()
                .port(port)
                .contentType(ContentType.JSON)
                .cookie(cookieByAdminLogin)
                .body(reservationRequest)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201)
                .header("Location", "/reservations/34")
                .body("id", equalTo(34))
                .body("date", equalTo(reservationRequest.date().toString()))
                .body("time.id", equalTo(reservationRequest.timeId().intValue()))
                .body("theme.id", equalTo(reservationRequest.themeId().intValue()))
                .body("member.id", equalTo(reservationRequest.memberId().intValue()));
    }

    @Test
    void 사용자_예약_추가() {
        Cookie cookieByUserLogin = getCookieByLogin(port, USER_EMAIL, USER_PASSWORD);
        String userAccessToken = cookieByUserLogin.getValue();
        String userId = jwtProvider.getSubject(userAccessToken);

        UserReservationRequest userReservationRequest = createUserReservationRequest();

        PaymentRequest paymentRequest = userReservationRequest.toPaymentRequest();
        PaymentResponse paymentResponse = new PaymentResponse(paymentRequest.paymentKey(), paymentRequest.orderId(), paymentRequest.amount());
        Mockito.when(tossPaymentsClient.requestPayment(paymentRequest)).thenReturn(paymentResponse);

        RestAssured
                .given().log().all()
                .port(port)
                .contentType(ContentType.JSON)
                .cookie(cookieByUserLogin)
                .body(userReservationRequest)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .header("Location", "/reservations/34")
                .body("id", equalTo(34))
                .body("date", equalTo(userReservationRequest.date().toString()))
                .body("time.id", equalTo(userReservationRequest.timeId().intValue()))
                .body("theme.id", equalTo(userReservationRequest.themeId().intValue()))
                .body("member.id", equalTo(Integer.parseInt(userId)));
    }

    @Test
    void 사용자_예약_대기_추가() {
        Cookie cookieByUserLogin = getCookieByLogin(port, USER_EMAIL, USER_PASSWORD);
        ReservationRequest reservationRequest = createReservationRequest();

        RestAssured
                .given().log().all()
                .port(port)
                .contentType(ContentType.JSON)
                .cookie(cookieByUserLogin)
                .body(reservationRequest)
                .when().post("/reservations/waiting")
                .then().log().all()
                .statusCode(201)
                .header("Location", "/reservations/34")
                .body("id", equalTo(34))
                .body("date", equalTo(reservationRequest.date().toString()))
                .body("time.id", equalTo(reservationRequest.timeId().intValue()))
                .body("theme.id", equalTo(reservationRequest.themeId().intValue()))
                .body("member.id", equalTo(reservationRequest.memberId().intValue()));
    }

    @Test
    void 예약_단일_조회() {
        RestAssured
                .given().log().all()
                .port(port)
                .when().get("/reservations/1")
                .then().log().all()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("date", equalTo(LocalDate.now().minusDays(9).toString()))
                .body("time.id", equalTo(3))
                .body("theme.id", equalTo(12))
                .body("member.id", equalTo(3));
    }

    @Test
    void 확정된_예약_전체_조회() {
        RestAssured
                .given().log().all()
                .port(port)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(30));
    }

    @Test
    void 사용자_예약_전체_조회() {
        Cookie cookieByUserLogin = getCookieByLogin(port, USER_EMAIL, USER_PASSWORD);

        RestAssured
                .given().log().all()
                .port(port)
                .cookie(cookieByUserLogin)
                .when().get("/reservations/mine")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(30));
    }

    @Test
    void 예약_조회시_조회필터_적용하여_조회() {
        Cookie cookieByAdminLogin = getCookieByLogin(port, ADMIN_EMAIL, ADMIN_PASSWORD);

        RestAssured
                .given().log().all()
                .port(port)
                .cookie(cookieByAdminLogin)
                .when().get("/admin/reservations/search?member=2&theme=1&dateFrom=&dateTo=")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(8))
                .body("member.id", everyItem(is(2)))
                .body("theme.id", everyItem(is(1)));
    }

    @Test
    void 예약_삭제() {
        RestAssured
                .given().log().all()
                .port(port)
                .when().delete("/reservations/32")
                .then().log().all()
                .statusCode(204);
    }

    private UserReservationRequest createUserReservationRequest() {
        return new UserReservationRequest(
                LocalDate.now().plusDays(10),
                1L,
                1L,
                "paymentKey",
                "orderId",
                BigDecimal.valueOf(1000),
                "paymentType"
        );
    }

    private ReservationRequest createReservationRequest() {
        return new ReservationRequest(
                LocalDate.now().plusDays(10),
                1L,
                1L,
                USER_ID
        );
    }
}
