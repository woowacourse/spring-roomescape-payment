package roomescape.exception;

public record PaymentErrorResponse(
        String code,
        String message,
        String data
) {
}
