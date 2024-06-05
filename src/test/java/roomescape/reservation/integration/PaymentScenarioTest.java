package roomescape.reservation.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import roomescape.auth.domain.Token;
import roomescape.auth.provider.CookieProvider;
import roomescape.client.payment.TossPaymentClient;
import roomescape.client.payment.dto.PaymentConfirmationFromTossDto;
import roomescape.client.payment.dto.PaymentConfirmationToTossDto;
import roomescape.exception.PaymentException;
import roomescape.model.IntegrationTest;
import roomescape.registration.domain.reservation.dto.ReservationRequest;
import roomescape.registration.domain.reservation.repository.ReservationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("예약 생성과 결제 시나리오 테스트")
class PaymentScenarioTest extends IntegrationTest {

    @MockBean
    TossPaymentClient tossPaymentClient;

    @Autowired
    ReservationRepository reservationRepository;

    @Test
    @DisplayName("정상 작동 - 예약 생성과 결제 승인에 예외가 발생하지 않으면, 예약이 정상적으로 저장된다.")
    void reservationSuccess() {
        ReservationRequest validReservationRequest = getValidReservationRequest();
        PaymentConfirmationToTossDto paymentConfirmationToTossDto = PaymentConfirmationToTossDto.from(validReservationRequest);
        given(tossPaymentClient.sendPaymentConfirm(paymentConfirmationToTossDto))
                .willReturn(getValidPaymentConfirmationFromTossDto());
        long beforeAdd = reservationRepository.count();

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(getLogInCookie())
                .body(validReservationRequest)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);
        long afterAdd = reservationRepository.count();
        assertThat(beforeAdd + 1).isEqualTo(afterAdd);
    }

    @Test
    @DisplayName("예외 발생 - 예약 생성이 실패하면, 결제 승인 함수가 호출되지 않는다.")
    void givenReservationFail_thenPaymentNotDone() {
        ReservationRequest invalidReservationRequest = getInvalidReservationRequest();
        PaymentConfirmationToTossDto paymentConfirmationToTossDto = PaymentConfirmationToTossDto.from(invalidReservationRequest);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(getLogInCookie())
                .body(invalidReservationRequest)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);

        then(tossPaymentClient)
                .should(times(0))
                .sendPaymentConfirm(paymentConfirmationToTossDto);
    }

    @Test
    @DisplayName("예외 발생 - 결제 승인이 되지 않으면, 예약 생성이 되지 않는다.")
    void givenPaymentFail_thenReservationSaveNotDone() {
        ReservationRequest validReservationRequest = getValidReservationRequest();
        PaymentConfirmationToTossDto paymentConfirmationToTossDto = PaymentConfirmationToTossDto.from(validReservationRequest);
        willThrow(new PaymentException(HttpStatus.BAD_REQUEST, "결제 승인할 때 이 예외를 던지게 설정되어있다."))
                .given(tossPaymentClient)
                .sendPaymentConfirm(paymentConfirmationToTossDto);
        long beforeAdd = reservationRepository.count();

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(getLogInCookie())
                .body(validReservationRequest)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);

        long afterAdd = reservationRepository.count();
        assertThat(beforeAdd).isEqualTo(afterAdd);
    }

    private String getLogInCookie() {
        Token token = tokenProvider.getAccessToken(1);
        return CookieProvider.setCookieFrom(token).toString();
    }

    private ReservationRequest getValidReservationRequest() {
        return new ReservationRequest(
                LocalDate.now().plusDays(2), 1L, 1L,
                "test-payment-type", "test-payment-key", "test-order-id", 1);
    }

    private ReservationRequest getInvalidReservationRequest() {
        return new ReservationRequest(
                LocalDate.now().minusDays(2), 1L, 1L,
                "test-payment-type", "test-payment-key", "test-order-id", 1);
    }

    private PaymentConfirmationFromTossDto getValidPaymentConfirmationFromTossDto() {
        return new PaymentConfirmationFromTossDto(
                "test-payment-key", "test-order-id", 10000L, "DONE",
                LocalDateTime.now().plusDays(7L)
        );
    }
}
