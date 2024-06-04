package roomescape.service.payment.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentRequest(
        @NotBlank(message = "결제 키가 유효하지 않습니다.") String paymentKey,
        @NotBlank(message = "주문 번호가 유효하지 않습니다.") String orderId,
        java.math.BigDecimal amount,
        @NotBlank(message = "결제 수단이 유효하지 않습니다.") String paymentType
) {
}
