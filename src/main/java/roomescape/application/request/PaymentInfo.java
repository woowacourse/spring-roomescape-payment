package roomescape.application.request;

public record PaymentInfo(
        String paymentKey,
        String orderId,
        long amount
) {
}
