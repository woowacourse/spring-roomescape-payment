package roomescape.payment.dto;

import java.math.BigDecimal;
import roomescape.member.domain.Member;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Schedule;

public record PaymentRequest(
        Long reservationId,
        String paymentKey,
        String orderId,
        BigDecimal amount
) {
    public Payment createPayment(Member member, Schedule schedule) {
        return new Payment(paymentKey, amount, member, schedule);
    }
}
