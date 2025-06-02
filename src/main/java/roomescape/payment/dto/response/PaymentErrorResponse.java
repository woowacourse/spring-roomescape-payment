package roomescape.payment.dto.response;

public record PaymentErrorResponse(
        String code,
        String message
) {
}
