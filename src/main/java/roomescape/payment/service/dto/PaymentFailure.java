package roomescape.payment.service.dto;

public record PaymentFailure(
        String code,
        String message
) {
}
