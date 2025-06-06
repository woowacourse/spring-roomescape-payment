package roomescape.reservation.dto.response;

public record PaymentErrorResponse(
        String code,
        String message
) {
}
