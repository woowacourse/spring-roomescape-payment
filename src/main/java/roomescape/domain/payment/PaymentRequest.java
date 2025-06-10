package roomescape.domain.payment;

public record PaymentRequest(
    String paymentKey,
    String orderId,
    int amount
) {

}
