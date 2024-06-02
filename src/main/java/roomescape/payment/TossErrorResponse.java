package roomescape.payment;

public record TossErrorResponse(
        String code,
        String message
) {
}
