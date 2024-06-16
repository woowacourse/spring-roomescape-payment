package roomescape.application.dto.request.payment;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record PaymentRequest(
        @Positive(message = "결제 금액은 0 이하일 수 없습니다.") BigDecimal amount,
        String orderId,
        String paymentKey
) {
}
