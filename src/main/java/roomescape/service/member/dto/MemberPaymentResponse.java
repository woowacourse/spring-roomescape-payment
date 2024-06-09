package roomescape.service.member.dto;

import roomescape.domain.payment.Payment;

public record MemberPaymentResponse(String paymentKey, long amount) {
    public static MemberPaymentResponse of(Payment payment) {
        if (payment == null) {
            return new MemberPaymentResponse("", 0);
        }
        return new MemberPaymentResponse(payment.getPaymentKey(), payment.getAmount());
    }
}
