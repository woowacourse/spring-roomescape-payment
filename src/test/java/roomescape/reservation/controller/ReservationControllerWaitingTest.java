package roomescape.reservation.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import roomescape.auth.service.TokenProvider;
import roomescape.fixture.ReservationFixture;
import roomescape.payment.infra.PaymentClient;
import roomescape.payment.dto.PaymentResponse;
import roomescape.reservation.controller.dto.ReservationPaymentRequest;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationSlot;
import roomescape.util.ControllerTest;

import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static roomescape.fixture.MemberFixture.getMemberTacan;

class ReservationControllerWaitingTest extends ControllerTest {

    @Autowired
    TokenProvider tokenProvider;

    @SpyBean
    PaymentClient paymentClient;

    String token;

    @BeforeEach
    void beforeEach() {
        token = tokenProvider.createAccessToken(getMemberTacan().getEmail());
        BDDMockito.doReturn(new PaymentResponse("test", "test", 1000L, "test", "test", "test"))
                .when(paymentClient)
                .confirm(any());
    }

    @Test
    @DisplayName("예약이 대기를 하는 경우 예약이 된다")
    void waiting() {
        //given
        Reservation bookedReservation = ReservationFixture.getBookedReservation();
        ReservationSlot alreadBookedReservationSlot = bookedReservation.getReservationSlot();

        ReservationPaymentRequest reservationPaymentRequest = new ReservationPaymentRequest(
                alreadBookedReservationSlot.getDate().format(DateTimeFormatter.ISO_DATE),
                alreadBookedReservationSlot.getTime().getId(),
                alreadBookedReservationSlot.getTheme().getId(),
                "test",
                "test",
                1000L,
                "test"
        );

        //when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(reservationPaymentRequest)
                .when().post("/reservations/waiting")
                .then().log().all()
                .statusCode(201);
    }

    @DisplayName("예약 대기인 예약을 삭제한다.")
    @Test
    void delete() {

        //given
        Reservation reservation = ReservationFixture.getBookedReservation();
        ReservationSlot bookedReservationSlot = reservation.getReservationSlot();
        ReservationPaymentRequest reservationPaymentRequest = new ReservationPaymentRequest(
                bookedReservationSlot.getDate().format(DateTimeFormatter.ISO_DATE),
                bookedReservationSlot.getTime().getId(),
                bookedReservationSlot.getTheme().getId(),
                "test",
                "test",
                1000L,
                "test"
        );

//        when & then
        String location = RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(reservationPaymentRequest)
                .when().post("/reservations/waiting")
                .then().log().all()
                .statusCode(201)
                .extract()
                .header("Location");

        RestAssured.given().log().all()
                .cookie("token", token)
                .when().delete(location)
                .then().log().all()
                .statusCode(204);
    }
}
