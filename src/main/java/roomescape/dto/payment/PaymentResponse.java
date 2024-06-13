package roomescape.dto.payment;

public record PaymentResponse(
        String paymentKey,
        Integer totalAmount
) {
}
