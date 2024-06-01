package roomescape.application.dto.request;

import java.math.BigDecimal;

public record PaymentApiRequest(String paymentKey, String orderId, BigDecimal amount) {

    public static PaymentApiRequest from(PaymentRequest paymentRequest) {
        return new PaymentApiRequest(paymentRequest.paymentKey(), paymentRequest.orderId(), paymentRequest.amount());
    }
}
