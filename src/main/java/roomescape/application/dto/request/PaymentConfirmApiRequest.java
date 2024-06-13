package roomescape.application.dto.request;

import java.math.BigDecimal;

public record PaymentConfirmApiRequest(String paymentKey, String orderId, BigDecimal amount) {

    public static PaymentConfirmApiRequest from(PaymentRequest paymentRequest) {
        return new PaymentConfirmApiRequest(paymentRequest.paymentKey(), paymentRequest.orderId(),
                paymentRequest.amount());
    }
}
