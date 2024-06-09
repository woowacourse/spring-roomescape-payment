package roomescape.payment.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import roomescape.member.model.Member;
import roomescape.payment.model.PaymentHistory;
import roomescape.reservation.model.Reservation;

public record PaymentConfirmResponse(String orderId, String paymentKey, Long totalAmount, String approvedAt) {

    public PaymentHistory toPaymentHistory(final Reservation reservation, final Member member) {
        LocalDateTime convertedDate = LocalDateTime.parse(approvedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return new PaymentHistory(orderId, paymentKey, totalAmount, convertedDate, reservation, member);
    }
}
