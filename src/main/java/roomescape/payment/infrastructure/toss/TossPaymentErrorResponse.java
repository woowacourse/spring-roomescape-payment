package roomescape.payment.infrastructure.toss;

public record TossPaymentErrorResponse(
        String code,
        String message
) {

}
