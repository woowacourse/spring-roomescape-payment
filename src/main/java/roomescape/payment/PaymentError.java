package roomescape.payment;

public record PaymentError(
        String code,
        String message
) {
}
