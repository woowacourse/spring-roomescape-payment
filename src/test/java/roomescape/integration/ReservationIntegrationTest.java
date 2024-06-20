package roomescape.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.exception.payment.PaymentConfirmErrorCode;
import roomescape.exception.payment.PaymentConfirmException;
import roomescape.service.payment.PaymentStatus;
import roomescape.service.payment.dto.PaymentCancelOutput;
import roomescape.service.payment.dto.PaymentConfirmInput;
import roomescape.service.payment.dto.PaymentConfirmOutput;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class ReservationIntegrationTest extends IntegrationTest {
    @Nested
    @DisplayName("예약 목록 조회 API")
    class FindAllReservation {
        Theme firstTheme;
        Member user;

        @BeforeEach
        void setUp() {
            ReservationTime time = timeFixture.createFutureTime();
            firstTheme = themeFixture.createFirstTheme();
            user = memberFixture.createUserMember();
            Theme secondTheme = themeFixture.createSecondTheme();
            Member admin = memberFixture.createAdminMember();
            reservationFixture.createPastReservation(time, firstTheme, user);
            reservationFixture.createFutureReservation(time, firstTheme, admin);
            reservationFixture.createPastReservation(time, secondTheme, user);
            reservationFixture.createFutureReservation(time, secondTheme, admin);
        }

        @Test
        void 예약_목록을_조회할_수_있다() {
            RestAssured.given().log().all()
                    .cookies(cookieProvider.createAdminCookies())
                    .when().get("/reservations")
                    .then().log().all()
                    .statusCode(200)
                    .body("reservations.size()", is(4));
        }

        @Test
        void 예약_목록을_예약자별로_필터링해_조회할_수_있다() {
            RestAssured.given().log().all()
                    .cookies(cookieProvider.createAdminCookies())
                    .when().get("/reservations?memberId=" + user.getId())
                    .then().log().all()
                    .statusCode(200)
                    .body("reservations.size()", is(2));
        }

        @Test
        void 예약_목록을_테마별로_필터링해_조회할_수_있다() {
            RestAssured.given().log().all()
                    .cookies(cookieProvider.createAdminCookies())
                    .when().get("/reservations?themeId=" + firstTheme.getId())
                    .then().log().all()
                    .statusCode(200)
                    .body("reservations.size()", is(2));
        }

        @Test
        void 예약_목록을_기간별로_필터링해_조회할_수_있다() {
            RestAssured.given().log().all()
                    .cookies(cookieProvider.createAdminCookies())
                    .when().get("/reservations?dateFrom=2000-04-01&dateTo=2000-04-07")
                    .then().log().all()
                    .statusCode(200)
                    .body("reservations.size()", is(2));
        }
    }

    @Nested
    @DisplayName("내 예약 목록 조회 API")
    class FindMyReservation {
        @Test
        void 내_예약_목록을_조회할_수_있다() {
            ReservationTime time = timeFixture.createFutureTime();
            Theme theme = themeFixture.createFirstTheme();
            Member member = memberFixture.createUserMember();
            Reservation reservation = reservationFixture.createFutureReservation(time, theme, member);
            paymentFixture.createPayment(reservation);
            waitingFixture.createWaiting(reservation, member);

            RestAssured.given().log().all()
                    .cookies(cookieProvider.createUserCookies())
                    .when().get("/reservations-mine")
                    .then().log().all()
                    .statusCode(200)
                    .body("reservations.size()", is(2));
        }
    }

    @Nested
    @DisplayName("사용자 예약 추가 API")
    class SaveReservation {
        ReservationTime time;
        Theme theme;
        Member member;
        Map<String, String> params = new HashMap<>();

        @BeforeEach
        void setUp() {
            time = timeFixture.createFutureTime();
            theme = themeFixture.createFirstTheme();
            member = memberFixture.createUserMember();
            params.put("themeId", time.getId().toString());
            params.put("timeId", theme.getId().toString());
            params.put("paymentKey", "testPaymentKey");
            params.put("orderId", "testOrderId");
            params.put("amount", "1000");
        }

        @Test
        void 결제_성공_시_예약을_추가할_수_있다() {
            params.put("date", "2000-04-07");
            given(paymentClient.confirmPayment(any())).willReturn(
                    new PaymentConfirmOutput("paymentKey", "orderId", "orderName",
                            1000, ZonedDateTime.now(), ZonedDateTime.now(), PaymentStatus.DONE));

            RestAssured.given().log().all()
                    .cookies(cookieProvider.createUserCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/reservations")
                    .then().log().all()
                    .statusCode(201)
                    .header("Location", "/reservations/1")
                    .body("id", is(1));
        }

        @Test
        void 결제_실패_시_예약을_추가할_수_없다() {
            params.put("date", "2000-04-07");
            given(paymentClient.confirmPayment(any()))
                    .willThrow(new PaymentConfirmException(PaymentConfirmErrorCode.UNKNOWN_PAYMENT_ERROR));

            RestAssured.given().log().all()
                    .cookies(cookieProvider.createUserCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/reservations")
                    .then().log().all()
                    .statusCode(502);
        }

        @Test
        void 필드가_빈_값이면_예약을_추가할_수_없다() {
            params.put("date", null);

            RestAssured.given().log().all()
                    .cookies(cookieProvider.createUserCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/reservations")
                    .then().log().all()
                    .statusCode(400);
        }

        @Test
        void 날짜의_형식이_다르면_예약을_추가할_수_없다() {
            params.put("date", "2000-13-07");

            RestAssured.given().log().all()
                    .cookies(cookieProvider.createUserCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/reservations")
                    .then().log().all()
                    .statusCode(400);
        }

        @Test
        void 시간대와_테마가_똑같은_중복된_예약은_추가할_수_없다() {
            Reservation reservation = reservationFixture.createFutureReservation(time, theme, member);
            params.put("date", reservation.getDate().toString());

            RestAssured.given().log().all()
                    .cookies(cookieProvider.createUserCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/reservations")
                    .then().log().all()
                    .statusCode(409);
        }

        @Test
        void 지나간_날짜와_시간에_대한_예약은_추가할_수_없다() {
            params.put("date", "2000-04-06");

            RestAssured.given().log().all()
                    .cookies(cookieProvider.createUserCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/reservations")
                    .then().log().all()
                    .statusCode(400);
        }
    }

    @Nested
    @DisplayName("관리자 예약 추가 API")
    class SaveAdminReservation {
        Map<String, String> params = new HashMap<>();

        @BeforeEach
        void setUp() {
            ReservationTime time = timeFixture.createFutureTime();
            Theme theme = themeFixture.createFirstTheme();
            Member member = memberFixture.createUserMember();
            memberFixture.createAdminMember();
            params.put("themeId", theme.getId().toString());
            params.put("timeId", time.getId().toString());
            params.put("memberId", member.getId().toString());
            params.put("date", "2000-04-07");
        }

        @Test
        void 관리자는_선택한_사용자_id로_예약을_추가할_수_있다() {
            RestAssured.given().log().all()
                    .cookies(cookieProvider.createAdminCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/admin/reservations")
                    .then().log().all()
                    .statusCode(201)
                    .header("Location", "/reservations/1")
                    .body("id", is(1));
        }

        @Test
        void 관리자가_아닌_일반_사용자가_사용시_예외가_발생한다() {
            RestAssured.given().log().all()
                    .cookies(cookieProvider.createUserCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/admin/reservations")
                    .then().log().all()
                    .statusCode(403);
        }
    }

    @Nested
    @DisplayName("예약 취소 API")
    class CancelReservation {
        Member member;
        Reservation reservation;

        @BeforeEach
        void setUp() {
            ReservationTime time = timeFixture.createFutureTime();
            Theme theme = themeFixture.createFirstTheme();
            member = memberFixture.createUserMember();
            reservation = reservationFixture.createFutureReservation(time, theme, member);
            paymentFixture.createPayment(reservation);
            memberFixture.createAdminMember();
        }

        @Test
        void 예약_id로_예약을_취소할_수_있다() {
            given(paymentClient.cancelPayment(any()))
                    .willReturn(new PaymentCancelOutput(
                            "paymentKey", "orderId", "orderName", PaymentStatus.CANCELED, ZonedDateTime.now(), ZonedDateTime.now()));

            RestAssured.given().log().all()
                    .cookies(cookieProvider.createAdminCookies())
                    .when().delete("/reservations/" + reservation.getId() + "/cancel")
                    .then().log().all()
                    .statusCode(204);
        }

        @Test
        void 존재하지_않는_예약_id로_예약을_취소할_수_없다() {
            long wrongReservationId = 10L;

            RestAssured.given().log().all()
                    .cookies(cookieProvider.createAdminCookies())
                    .when().delete("/reservations/" + wrongReservationId + "/cancel")
                    .then().log().all()
                    .statusCode(404);
        }

        @Test
        void 예약자가_아닌_사용자는_예약을_취소할_수_없다() {
            Member anotherMember = memberFixture.createUserMember("another@gmail.com");

            RestAssured.given().log().all()
                    .cookies(cookieProvider.createUserCookies(anotherMember.getEmail().address()))
                    .when().delete("/reservations/" + reservation.getId() + "/cancel")
                    .then().log().all()
                    .statusCode(403);
        }
    }

    @Nested
    @DisplayName("예약 결제 API")
    class PayReservation {
        Member member;
        Reservation reservation;

        @BeforeEach
        void setUp() {
            ReservationTime time = timeFixture.createFutureTime();
            Theme theme = themeFixture.createFirstTheme();
            member = memberFixture.createUserMember();
            reservation = reservationFixture.createPaymentWaitingReservation(time, theme, member);
        }

        @Test
        void 결제_대기중인_예약을_결제하면_예약상태로_변경되고_결제정보가_추가된다() {
            given(paymentClient.confirmPayment(any())).willReturn(
                    new PaymentConfirmOutput("paymentKey", "orderId", "orderName",
                            1000, ZonedDateTime.now(), ZonedDateTime.now(), PaymentStatus.DONE));

            PaymentConfirmInput paymentConfirmInput = new PaymentConfirmInput("orderId", 1000, "paymentKey");

            RestAssured.given().log().all()
                    .cookies(cookieProvider.createUserCookies())
                    .when()
                    .body(paymentConfirmInput)
                    .contentType(ContentType.JSON)
                    .post("/reservations/" + reservation.getId() + "/payment")
                    .then().log().all()
                    .statusCode(200)
                    .body("id", is(reservation.getId().intValue()));
            ;
        }
    }

    @Nested
    @DisplayName("결제대기 예약 삭제 API")
    class DeletePaymentWaitingReservation {
        Member member;
        Reservation reservation;

        @BeforeEach
        void setUp() {
            ReservationTime time = timeFixture.createFutureTime();
            Theme theme = themeFixture.createFirstTheme();
            member = memberFixture.createUserMember();
            reservation = reservationFixture.createPaymentWaitingReservation(time, theme, member);
            memberFixture.createAdminMember();
        }

        @Test
        void 예약_id로_결제_대기_예약을_삭제할_수_있다() {
            RestAssured.given().log().all()
                    .cookies(cookieProvider.createAdminCookies())
                    .when().delete("/reservations/" + reservation.getId())
                    .then().log().all()
                    .statusCode(204);
        }

        @Test
        void 예약자가_아닌_사용자는_예약을_삭제할_수_없다() {
            Member anotherMember = memberFixture.createUserMember("another@gmail.com");

            RestAssured.given().log().all()
                    .cookies(cookieProvider.createUserCookies(anotherMember.getEmail().address()))
                    .when().delete("/reservations/" + reservation.getId())
                    .then().log().all()
                    .statusCode(403);
        }
    }
}
