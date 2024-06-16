package roomescape.payment.dto;

import java.math.BigDecimal;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderId,
        BigDecimal amount
) {
    public static PaymentConfirmRequest from(PaymentRequest request) {
        return new PaymentConfirmRequest(request.paymentKey(), request.orderId(), request.amount());
    }
}
