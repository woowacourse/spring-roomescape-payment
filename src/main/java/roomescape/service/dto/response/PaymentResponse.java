package roomescape.service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import roomescape.domain.reservation.Payment;
import roomescape.domain.reservation.Reservation;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentResponse(
        String paymentKey,
        String orderId,
        Integer totalAmount
) {
    public Payment toPayment(Reservation reservation) {
        return new Payment(reservation, paymentKey, orderId, totalAmount);
    }
}
