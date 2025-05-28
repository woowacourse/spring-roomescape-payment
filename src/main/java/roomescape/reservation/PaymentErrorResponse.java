package roomescape.reservation;

public record PaymentErrorResponse(
        String code,
        String message
) {
}
