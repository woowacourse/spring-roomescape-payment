package roomescape.payment.dto;

public record TossErrorResponse(
        String code,
        String message
) {
}
