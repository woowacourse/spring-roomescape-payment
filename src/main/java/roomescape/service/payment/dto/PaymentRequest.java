package roomescape.service.payment.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record PaymentRequest(
        @NotBlank(message = "결제 키가 유효하지 않습니다.") String paymentKey,
        @NotBlank(message = "주문 번호가 유효하지 않습니다.") String orderId,
        BigDecimal amount,
        @NotBlank(message = "결제 수단이 유효하지 않습니다.") String paymentType
) {
}
