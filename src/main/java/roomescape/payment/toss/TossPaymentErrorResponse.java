package roomescape.payment.toss;

public record TossPaymentErrorResponse(
        String code,
        String message
) {

}