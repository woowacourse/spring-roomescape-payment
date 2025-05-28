package roomescape.reservation.payment.dto.response;

public record TossPaymentErrorResponse(
        String code,
        String message
) {
}
