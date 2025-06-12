package roomescape.payment.toss.exception;

public record TossPaymentErrorResponse(
        String code,
        String message
) {

}
