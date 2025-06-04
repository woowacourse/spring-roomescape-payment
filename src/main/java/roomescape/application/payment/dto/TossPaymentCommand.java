package roomescape.application.payment.dto;

public record TossPaymentCommand(
        String paymentKey,
        String orderId,
        Long amount
) {

    public TossPaymentValidationCommand toValidationCommand() {
        return new TossPaymentValidationCommand(orderId, amount);
    }
}
