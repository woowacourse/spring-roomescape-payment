package roomescape.payment.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;

public record RestClientPaymentApproveResponse(String paymentKey, String orderId, BigDecimal totalAmount,
                                               ZonedDateTime approvedAt) {

    public Payment createPayment(Reservation reservation) {
        return new Payment(reservation, paymentKey, totalAmount, orderId, approvedAt.toLocalDateTime());
    }
}
