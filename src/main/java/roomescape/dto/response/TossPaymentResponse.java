package roomescape.dto.response;

public record TossPaymentResponse(
        String paymentKey,
        String orderId,
        String orderName,
        int totalAmount,
        PaymentStatus status
) {
    public static enum PaymentStatus {
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
