package roomescape.service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentCancelResponse(
        String paymentKey,
        String orderId,
        String orderName
) {
}
