package roomescape.payment.infraStructure.dto;

public record ConfirmPaymentResponse(
        Integer totalAmount,
        String paymentKey,
        PaymentFailure failure
) {
}
