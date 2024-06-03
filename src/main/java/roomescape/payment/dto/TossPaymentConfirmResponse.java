package roomescape.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossPaymentConfirmResponse(
        String paymentKey,
        String orderId,
        String orderName,
        Long totalAmount
) {
}
