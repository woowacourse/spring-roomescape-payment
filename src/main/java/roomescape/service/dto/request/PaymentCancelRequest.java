package roomescape.service.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record PaymentCancelRequest(
        @JsonIgnore
        String paymentKey,
        String cancelReason
) {
}
