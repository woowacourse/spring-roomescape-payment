package roomescape.payment.infraStructure.dto;

public record PaymentFailure(
        String code,
        String message
) {
}
