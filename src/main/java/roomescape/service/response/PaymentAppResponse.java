package roomescape.service.response;

import java.math.BigDecimal;
import roomescape.domain.Payment;

public record PaymentAppResponse(String paymentKey, String orderId, BigDecimal amount) {

    public static PaymentAppResponse from(Payment payment) {
        if (payment == null) {
            return null;
        }
        return new PaymentAppResponse(payment.getPaymentKey(), payment.getOrderId(), payment.getAmount());
    }
}
