package roomescape.payment.infrastructure.dto;

public record PaymentFailure(
        String code,
        String message
) {
}
