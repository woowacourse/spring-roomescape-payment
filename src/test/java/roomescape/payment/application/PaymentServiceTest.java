package roomescape.payment.application;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.payment.application.service.PaymentClient;
import roomescape.payment.application.service.PaymentService;
import roomescape.payment.domain.repository.PaymentRepository;
import roomescape.payment.infrastructure.TossPaymentClient;
import roomescape.payment.infrastructure.dto.TossPaymentResponse;
import roomescape.reservation.presentation.dto.ReservationRequest;

class PaymentServiceTest {

    @Test
    @DisplayName("결제 승인 API를 1회 호출한다")
    void processPaymentRequestTest() {
        // given
        PaymentClient mockPaymentClient = mock(TossPaymentClient.class);
        PaymentRepository mockRepository = mock(PaymentRepository.class);

        PaymentService paymentService = new PaymentService(mockRepository, mockPaymentClient);
        ReservationRequest reservationRequest = new ReservationRequest(
                LocalDate.now(),
                1L,
                1L,
                "",
                "",
                1,
                "");
        TossPaymentResponse paymentResponse = new TossPaymentResponse(
                "",
                "",
                0,
                "",
                "2025-01-01T00:00:00+09:00");

        // when
        when(mockPaymentClient.approve(reservationRequest)).thenReturn(paymentResponse);
        paymentService.processPaymentRequest(reservationRequest);

        // then
        verify(mockPaymentClient, times(1)).approve(reservationRequest);
    }
}
