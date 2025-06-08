package roomescape.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 처리 요청 DTO")
public record PaymentProcessRequest(
        @Schema(description = "결제 키", example = "tosspayments_123456789")
        String paymentKey,

        @Schema(description = "주문 번호", example = "order_123456789")
        String orderId,

        @Schema(description = "결제 금액", example = "30000")
        String amount
) {
}
