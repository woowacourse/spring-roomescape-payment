package roomescape.presentation.response;

import jakarta.annotation.Nullable;
import roomescape.domain.payment.Payment;

public record PaymentResponse(
    String paymentKey,
    long amount
) {

    public static PaymentResponse from(@Nullable final Payment payment) {
        if (payment == null) {
            return new PaymentResponse(null, 0);
        }
        return new PaymentResponse(payment.paymentKey(), payment.amount());
    }
}
