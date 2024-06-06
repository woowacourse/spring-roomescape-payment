package roomescape.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import roomescape.domain.PaymentInfo;
import roomescape.domain.Reservation;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentConfirmResponse(
        String paymentKey,
        String orderId,
        String orderName,
        Long totalAmount
) {

    public PaymentInfo toPayment(final Reservation reservation) {
        return new PaymentInfo(null, orderId, paymentKey, orderName, totalAmount, reservation);
    }
}
