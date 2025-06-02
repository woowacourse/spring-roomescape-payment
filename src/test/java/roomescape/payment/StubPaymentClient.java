package roomescape.payment;

import roomescape.payment.application.service.PaymentClient;
import roomescape.payment.infrastructure.dto.TossPaymentResponse;
import roomescape.reservation.presentation.dto.ReservationRequest;

public class StubPaymentClient implements PaymentClient {
    @Override
    public TossPaymentResponse approve(final ReservationRequest reservationRequest) {
        return null;
    }
}
