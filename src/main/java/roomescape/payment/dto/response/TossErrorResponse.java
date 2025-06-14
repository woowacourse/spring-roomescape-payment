package roomescape.payment.dto.response;

public record TossErrorResponse(
        String code,
        String message
) {
}
