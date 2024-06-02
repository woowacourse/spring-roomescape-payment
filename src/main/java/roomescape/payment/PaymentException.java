package roomescape.payment;

public class PaymentException extends RuntimeException {

    public PaymentException(TossErrorResponse tossErrorResponse) {
        super(tossErrorResponse.message());
    }
}
