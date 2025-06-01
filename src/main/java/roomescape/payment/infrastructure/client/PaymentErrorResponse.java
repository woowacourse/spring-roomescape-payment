package roomescape.payment.infrastructure.client;

public record PaymentErrorResponse(
        String code,
        String message,
        String data
) {
}
