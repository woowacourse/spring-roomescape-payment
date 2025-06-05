package roomescape.domain.payment;

public record PaymentRequest(
        String paymentKey,
        String orderId,
        long amount
) {

}
