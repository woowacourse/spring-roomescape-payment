package roomescape.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토스 결제 확인 요청 DTO")
public record TossPaymentConfirmRequest(
        @Schema(description = "결제 금액", example = "1000")
        int amount,

        @Schema(description = "주문 ID", example = "order12345")
        String orderId,

        @Schema(description = "결제 키", example = "paymentKey12345")
        String paymentKey
) implements PaymentConfirmRequest {
}
