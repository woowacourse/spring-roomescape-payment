package roomescape.payment.processor.toss;

public record TossPaymentConfirmError(
    String code,
    String message
) {
}
