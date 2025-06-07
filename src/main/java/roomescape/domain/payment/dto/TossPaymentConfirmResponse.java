package roomescape.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토스 결제 확인 응답 DTO")
public record TossPaymentConfirmResponse(
        @Schema(description = "주문 ID", example = "order-12345")
        String orderId,

        @Schema(description = "결제 키", example = "paymentKey-12345")
        String paymentKey,

        @Schema(description = "총 결제 금액", example = "10000")
        int totalAmount
) implements PaymentConfirmResponse {

    @Override
    public String getOrderId() {
        return orderId;
    }

    @Override
    public String getPaymentKey() {
        return paymentKey;
    }

    @Override
    public int getTotalAmount() {
        return totalAmount;
    }
}