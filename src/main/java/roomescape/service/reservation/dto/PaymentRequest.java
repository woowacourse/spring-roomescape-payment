package roomescape.service.reservation.dto;

import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull(message = "결제 정보를 입력해주세요.") String paymentKey,
        @NotNull(message = "결제 정보를 입력해주세요.") String orderId,
        @NotNull(message = "결제 정보를 입력해주세요.") Long amount
) {
}
