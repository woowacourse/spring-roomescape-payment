package roomescape.payment.dto.resonse;

public record PaymentErrorResponse(
        String code,
        String message
) {
}
