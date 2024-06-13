package roomescape.payment.dto;

import java.math.BigDecimal;
import roomescape.payment.domain.Payment;

public record PaymentResponse(Long reservationId, Long memberId, String paymentKey, BigDecimal amount) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(payment.getReservationId(), payment.getMemberId(), payment.getPaymentKey(), payment.getAmount());
    }
}
