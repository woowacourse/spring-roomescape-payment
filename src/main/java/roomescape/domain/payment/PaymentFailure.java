package roomescape.domain.payment;

public record PaymentFailure(
        String code,
        String message
) {
}
