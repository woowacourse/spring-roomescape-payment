package roomescape.payment.dto;

import roomescape.member.model.Member;
import roomescape.payment.model.PaymentHistory;
import roomescape.reservation.model.Reservation;

public record PaymentConfirmResponse(String paymentKey, Long totalAmount) {

    public PaymentHistory toPaymentHistory(final Reservation reservation, final Member member) {
        return new PaymentHistory(paymentKey, totalAmount, reservation, member);
    }
}
