package roomescape.service.payment.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import roomescape.domain.payment.PaymentType;

public record PaymentRequest(
        @NotBlank(message = "결제 키가 유효하지 않습니다.") String paymentKey,
        @NotBlank(message = "주문 번호가 유효하지 않습니다.") String orderId,
        BigDecimal amount,
        PaymentType paymentType
) {
    public boolean isAdmin() {
        return paymentType().isAdmin();
    }
}
