package roomescape.dto.payment;

public record PaymentErrorResponse(
        String code,
        String message
) {
}
