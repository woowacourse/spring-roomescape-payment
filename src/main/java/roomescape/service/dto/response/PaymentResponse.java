package roomescape.service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentResponse(
        String paymentKey,
        String orderId,
        int amount
) {

}
