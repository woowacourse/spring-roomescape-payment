package roomescape.payment.dto;

import java.math.BigDecimal;
import roomescape.payment.domain.Payment;

public record PaymentResponse(String paymentKey, BigDecimal totalAmount) {

    public static PaymentResponse toResponse(Payment savedPayment) {
        return new PaymentResponse(savedPayment.getPaymentKey(), savedPayment.getTotalAmount());
    }
}
