package roomescape.application.response;

public record PaymentClientResponse(
        String paymentKey,
        String orderId,
        String orderName,
        long amount
) {
}
