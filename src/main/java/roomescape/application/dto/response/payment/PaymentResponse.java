package roomescape.application.dto.response.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.payment.Payment;

@Schema(name = "결제 정보")
public record PaymentResponse(
        @Schema(description = "결제 금액", example = "10000")
        Long totalAmount,

        @Schema(description = "결제 키", example = "paymentKey")
        String paymentKey,

        @Schema(description = "주문 ID", example = "orderId")
        String orderId,

        @Schema(description = "결제 상태", example = "status")
        String status,

        @Schema(description = "요청 시간", example = "requestedAt")
        String requestedAt,

        @Schema(description = "승인 시간", example = "approvedAt")
        String approvedAt
) {

    public static PaymentResponse empty() {
        return new PaymentResponse(0L, "", "", "", "", "");
    }

    public Payment toPayment() {
        return new Payment(totalAmount, paymentKey, orderId, requestedAt, approvedAt);
    }
}
