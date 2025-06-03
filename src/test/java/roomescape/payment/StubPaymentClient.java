package roomescape.payment;

import roomescape.payment.application.service.PaymentClient;
import roomescape.payment.domain.Payment;
import roomescape.payment.presentation.dto.PaymentRequest;
import roomescape.reservation.presentation.dto.ReservationRequest;

public class StubPaymentClient implements PaymentClient {
    @Override
    public Payment approve(final PaymentRequest paymentRequest) {
        return new Payment(
                paymentRequest.getPaymentKey(),
                paymentRequest.getOrderId(),
                paymentRequest.getAmount(),
                "NORMAL"
        );
    }
}
