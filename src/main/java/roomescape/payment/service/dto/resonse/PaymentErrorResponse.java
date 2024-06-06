package roomescape.payment.service.dto.resonse;

public record PaymentErrorResponse(
        String code,
        String message
) {
}
