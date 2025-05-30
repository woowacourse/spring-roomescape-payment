package roomescape.payment;

import roomescape.payment.application.service.PaymentClient;
import roomescape.payment.domain.Payment;
import roomescape.reservation.presentation.dto.ReservationRequest;

public class StubPaymentClient implements PaymentClient {
    @Override
    public Payment approve(final ReservationRequest reservationRequest) {
        return new Payment(
                reservationRequest.getPaymentKey(),
                reservationRequest.getOrderId(),
                reservationRequest.getAmount(),
                "NORMAL"
        );
    }
}
