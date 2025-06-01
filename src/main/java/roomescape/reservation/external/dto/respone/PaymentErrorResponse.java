package roomescape.reservation.external.dto.respone;

public record PaymentErrorResponse(
        String code,
        String message
) {
}
