package roomescape.dto.payment;

public record PaymentSaveRequest(
        String paymentKey,
        String orderId,
        Long amount
) {
}
