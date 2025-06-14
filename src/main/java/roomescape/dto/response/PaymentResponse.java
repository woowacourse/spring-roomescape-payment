package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Payment;

@Schema(description = "결제 응답 객체")
public record PaymentResponse(
        @Schema(description = "주문 ID")
        String orderId,

        @Schema(description = "결제 키")
        String paymentKey,

        @Schema(description = "총 결제 금액", example = "1000")
        int totalAmount
) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(payment.getOrderId(), payment.getPaymentKey(), payment.getAmount());
    }
}
