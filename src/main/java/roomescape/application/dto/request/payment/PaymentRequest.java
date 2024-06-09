package roomescape.application.dto.request.payment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "결제 정보")
public record PaymentRequest(
        @Schema(description = "결제 금액", example = "10000")
        Long amount,

        @Schema(description = "주문 ID", example = "1")
        String orderId,

        @Schema(description = "결제 키", example = "imp_1234567890")
        String paymentKey
) {
}
