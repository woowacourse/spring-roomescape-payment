package roomescape.payment.infrastructure;

public record TossPaymentsErrorResponse(
        String code,
        String message
) {
}
