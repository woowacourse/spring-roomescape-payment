package roomescape.payment.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 응답")
public record PaymentRequest(
        @Schema(description = "paymentKey", defaultValue = "paymentKey") String paymentKey,
        @Schema(description = "orderId", defaultValue = "orderId") String orderId,
        @Schema(description = "총 금액", defaultValue = "1000") BigDecimal amount) {
}
