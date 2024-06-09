package roomescape.service.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.TestFixture.USER_ID;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationRequestWithPayment;
import roomescape.infrastructure.tosspayments.TossPaymentsClient;
import roomescape.service.ServiceBaseTest;

class ReservationServiceTest extends ServiceBaseTest {

    @Autowired
    ReservationService reservationService;

    @MockBean
    TossPaymentsClient tossPaymentsClient;

    @Test
    void 정상_결제시_예약_등록() {
        // given
        ReservationRequestWithPayment reservationRequestWithPayment = new ReservationRequestWithPayment(
                LocalDate.now().plusDays(7),
                1L,
                1L,
                "paymentKey",
                "orderId",
                1000,
                "paymentType"
        );

        PaymentRequest paymentRequest = reservationRequestWithPayment.toPaymentRequest();
        PaymentResponse paymentResponse = new PaymentResponse(paymentRequest.paymentKey(), paymentRequest.orderId());
        Mockito.when(tossPaymentsClient.requestPayment(paymentRequest)).thenReturn(paymentResponse);

        // when
        ReservationResponse reservationResponse = reservationService.registerReservationWithPayment(
                reservationRequestWithPayment,
                USER_ID
        );

        // then
        assertThat(reservationResponse).isNotNull();
    }
}
