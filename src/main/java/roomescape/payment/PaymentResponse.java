package roomescape.payment;

public record PaymentResponse(
        String paymentKey,
        String status,
        String orderId,
        Long totalAmount,
        String method

) {
}
