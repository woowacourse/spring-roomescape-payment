package roomescape.dto.payment;

public record PaymentRequest(
        String orderId,
        Integer amount,
        String paymentKey
) {
}
