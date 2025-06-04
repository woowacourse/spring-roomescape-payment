package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record TossPaymentResponse(
        @Schema(example = "toss-payment-key")
        String paymentKey,

        @Schema(example = "toss-order-id")
        String orderId,

        @Schema(example = "toss-order-name")
        String orderName,

        @Schema(example = "10000")
        int totalAmount,

        @Schema(allowableValues = {"READY", "IN_PROGRESS", "DONE", "CANCELED"})
        PaymentStatus status
) {
    public enum PaymentStatus {
        READY,
        IN_PROGRESS,
        WAITING_FOR_DEPOSIT,
        DONE,
        CANCELED,
        PARTIAL_CANCELED,
        ABORTED,
        EXPIRED
    }
}
