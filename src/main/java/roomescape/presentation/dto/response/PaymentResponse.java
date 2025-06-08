package roomescape.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Payment;

@Schema(description = "결제 정보 응답 DTO")
public record PaymentResponse(
        @Schema(description = "결제 ID")
        Long id,

        @Schema(description = "결제 키", example = "tosspayments_123456789")
        String paymentKey,

        @Schema(description = "주문 번호", example = "order_123456789")
        String orderId,

        @Schema(description = "결제 금액", example = "30000")
        int totalAmount
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getPaymentKey(),
                payment.getOrderId(),
                payment.getTotalAmount()
        );
    }
}
