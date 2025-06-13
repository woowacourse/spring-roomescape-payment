package roomescape.payment.infrastructure.client;

public record TossPaymentErrorResponse(
        String code,
        String message,
        String data
) {

}
