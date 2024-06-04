package roomescape.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import roomescape.domain.Payment;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossPaymentConfirmResponse(
        String paymentKey,
        String orderId,
        String orderName,
        Long totalAmount
) {

    public Payment toPayment() {
        return new Payment(null, orderId, paymentKey, orderName, totalAmount);
    }
}
