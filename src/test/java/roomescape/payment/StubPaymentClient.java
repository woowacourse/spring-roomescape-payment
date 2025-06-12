package roomescape.payment;

import roomescape.payment.application.service.PaymentClient;
import roomescape.payment.infrastructure.dto.TossPaymentResponse;
import roomescape.reservation.presentation.dto.ReservationRequest;

public class StubPaymentClient implements PaymentClient {
    @Override
    public TossPaymentResponse approve(final ReservationRequest reservationRequest) {
        return new TossPaymentResponse(
                "test",
                "testOrderId",
                1000,
                "NORMAL",
                "2025-01-01T00:00:00+09:00");
    }
}
