package roomescape.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentConfirmResponse(
        String paymentKey,
        String orderId,
        String orderName,
        Long totalAmount
) {

    public Payment toPayment(final Reservation reservation) {
        return new Payment(null, orderId, paymentKey, orderName, totalAmount, reservation);
    }
}
