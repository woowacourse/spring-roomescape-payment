package roomescape.payment.dto.response;

import java.time.OffsetDateTime;
import roomescape.payment.domain.Payment;
import roomescape.reservation.dto.response.ReservationResponse;

public record ReservationPaymentResponse(Long id, String orderId, String paymentKey, Long totalAmount,
                                         ReservationResponse reservation, OffsetDateTime approvedAt) {

    public static ReservationPaymentResponse from(Payment saved) {
        return new ReservationPaymentResponse(saved.getId(), saved.getOrderId(), saved.getPaymentKey(),
                saved.getTotalAmount(), ReservationResponse.from(saved.getReservation()), saved.getApprovedAt());
    }
}
