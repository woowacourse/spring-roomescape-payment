package roomescape.payment.service.dto;

public record ConfirmPaymentResponse(
        Integer totalAmount,
        String paymentKey,
        PaymentFailure failure
) {
}
