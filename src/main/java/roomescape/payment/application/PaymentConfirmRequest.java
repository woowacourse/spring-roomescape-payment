package roomescape.payment.application;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentConfirmRequest(
        @NotNull(message = "결제 키는 비어있을 수 없습니다.")
        String paymentKey,
        @NotNull(message = "주문 Id는 비어있을 수 없습니다.")
        String orderId,
        @NotNull(message = "결제 금액은 비어있을 수 없습니다.")
        BigDecimal amount,
        @NotNull(message = "결제 타입은 비어있을 수 없습니다.")
        String paymentType) {
}
