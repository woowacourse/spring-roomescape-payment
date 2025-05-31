package roomescape.application.response;

public record PaymentResponse(
        String paymentKey,
        String orderId,
        String orderName,
        long amount
) {
}
