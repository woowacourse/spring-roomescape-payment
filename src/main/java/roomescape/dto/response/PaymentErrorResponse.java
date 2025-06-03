package roomescape.dto.response;

public record PaymentErrorResponse(
        String code,
        String message) {
}
