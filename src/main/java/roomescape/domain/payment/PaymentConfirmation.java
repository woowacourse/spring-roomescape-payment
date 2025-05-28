package roomescape.domain.payment;

public record PaymentConfirmation(
        String paymentKey,
        String orderId,
        String orderName,
        long totalAmount
) {

}
