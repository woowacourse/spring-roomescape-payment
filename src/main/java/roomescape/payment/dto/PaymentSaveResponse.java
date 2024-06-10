package roomescape.payment.dto;

import java.math.BigDecimal;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;

public record PaymentSaveResponse(String paymentKey, PaymentStatus status, BigDecimal amount) {

    public static PaymentSaveResponse toResponse(Payment payment) {
        return new PaymentSaveResponse(payment.getPaymentKey(), payment.getStatus(), payment.getTotalAmount());
    }
}
