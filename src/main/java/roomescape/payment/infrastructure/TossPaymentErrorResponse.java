package roomescape.payment.infrastructure;

public record TossPaymentErrorResponse(
        String code,
        String message
) {

}
