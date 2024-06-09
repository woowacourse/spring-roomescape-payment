package roomescape.domain.reservation.dto;

import roomescape.domain.payment.model.PaymentHistory;
import roomescape.domain.payment.model.PaymentStatus;
import roomescape.domain.reservation.model.Reservation;

import java.time.LocalDateTime;

public record SavePaymentHistoryRequest(
        String orderId,
        PaymentStatus paymentStatus,
        String orderName,
        Long totalAmount,
        LocalDateTime approvedAt,
        String paymentKey,
        String paymentProvider,
        Reservation reservation
) {
    public PaymentHistory toPaymentHistory() {
        return new PaymentHistory(
                orderId,
                paymentStatus,
                orderName,
                totalAmount,
                approvedAt,
                paymentKey,
                paymentProvider,
                reservation
        );
    }
}
