package roomescape.dto.response;

public record PaymentErrorResponse(
        String code,
        String message
) {
    public static PaymentErrorResponse from(String code, String message) {
        return new PaymentErrorResponse(code, message);
    }
}
