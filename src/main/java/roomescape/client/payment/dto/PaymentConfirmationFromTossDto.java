package roomescape.client.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentConfirmationFromTossDto(
        String paymentKey,
        String orderId,
        Long totalAmount,
        String status
) {
}
