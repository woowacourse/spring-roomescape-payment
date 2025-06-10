package roomescape.infrastructure.payment.toss;

public record TossPaymentConfirmError(
        String code,
        String message
) {
}
