package roomescape.payment.application;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.payment.application.service.PaymentClient;
import roomescape.payment.application.service.PaymentService;
import roomescape.payment.infrastructure.TossPaymentClient;
import roomescape.reservation.presentation.dto.ReservationRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PaymentServiceTest {

    @Test
    @DisplayName("결제 승인 API를 1회 호출한다")
    void approveTest() {
        // given
        PaymentClient mockPaymentClient = mock(TossPaymentClient.class);
        PaymentService paymentService = new PaymentService(mockPaymentClient);
        ReservationRequest reservationRequest = new ReservationRequest(
                LocalDate.now(),
                1L,
                1L,
                "",
                "",
                1,
                "");

        // when
        paymentService.approve(reservationRequest);

        // then
        verify(mockPaymentClient, times(1)).approve(reservationRequest);
    }
}
