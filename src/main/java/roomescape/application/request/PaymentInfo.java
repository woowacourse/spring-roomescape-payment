package roomescape.application.request;

public record PaymentInfo(
        String paymentKey,
        String orderId,
        String orderName,
        Long amount
) {
}
