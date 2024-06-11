package roomescape.payment.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import roomescape.payment.model.PaymentHistory;
import roomescape.reservation.model.Reservation;

public record PaymentConfirmResponse(String orderId, String paymentKey, String status, Long totalAmount,
                                     String approvedAt) {

    public PaymentHistory toPaymentHistory(final Reservation reservation) {
        LocalDateTime convertedDate = LocalDateTime.parse(approvedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return new PaymentHistory(orderId, paymentKey, status, totalAmount, convertedDate, reservation);
    }
}
