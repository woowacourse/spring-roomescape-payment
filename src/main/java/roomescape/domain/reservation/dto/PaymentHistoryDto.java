package roomescape.domain.reservation.dto;

import roomescape.domain.payment.model.PaymentHistory;
import roomescape.domain.payment.model.PaymentStatus;
import roomescape.domain.reservation.model.Reservation;

import java.time.LocalDateTime;

public record PaymentHistoryDto(
        PaymentStatus paymentStatus,
        String orderName,
        Long totalAmount,
        LocalDateTime approvedAt,
        String paymentKey,
        String paymentProvider,
        Reservation reservation
) {
    public PaymentHistory toModel() {
        return new PaymentHistory(
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
