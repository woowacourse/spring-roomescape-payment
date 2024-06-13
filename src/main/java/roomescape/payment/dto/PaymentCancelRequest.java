package roomescape.payment.dto;

import jakarta.validation.constraints.NotNull;

public record PaymentCancelRequest(
        @NotNull(message = "PaymentKey가 존재하지 않습니다.") String paymentKey
) {
}
