package roomescape.reservation.dto;

import java.math.BigDecimal;
import roomescape.payment.PaymentType;
import roomescape.payment.dto.PaymentCreateRequest;
import roomescape.reservation.domain.Reservation;

public record ReservationPaymentRequest(
        Long reservationId,
        String paymentKey,
        String orderId,
        BigDecimal amount,
        PaymentType paymentType
) {

    public PaymentCreateRequest createPaymentRequest(Reservation reservation) {
        return new PaymentCreateRequest(paymentKey, orderId, amount, reservation);
    }
}
