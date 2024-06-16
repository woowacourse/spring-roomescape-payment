package roomescape.application.dto.request.reservation;

import java.math.BigDecimal;
import roomescape.application.dto.request.payment.PaymentRequest;

public record ReservationPaymentRequest(
        Long reservationId,
        BigDecimal amount,
        String orderId,
        String paymentKey
) {

    public PaymentRequest toPaymentRequest() {
        return new PaymentRequest(amount, orderId, paymentKey);
    }
}
