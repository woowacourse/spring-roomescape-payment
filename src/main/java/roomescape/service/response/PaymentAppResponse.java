package roomescape.service.response;

import java.math.BigDecimal;
import java.util.List;
import roomescape.domain.Payment;

public record PaymentAppResponse(String paymentKey, String orderId, BigDecimal amount) {

    public static PaymentAppResponse from(Payment payment) {
        return new PaymentAppResponse(payment.getPaymentKey(), payment.getOrderId(), payment.getAmount());
    }

    public static List<PaymentAppResponse> from(List<Payment> payments) {
        return payments.stream()
                .map(PaymentAppResponse::from)
                .toList();
    }
}
