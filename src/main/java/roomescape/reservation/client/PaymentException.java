package roomescape.reservation.client;

public class PaymentException extends RuntimeException {
    private final TossErrorResponse tossErrorResponse;

    public PaymentException(TossErrorResponse tossErrorResponse) {
        super(tossErrorResponse.message());
        this.tossErrorResponse = tossErrorResponse;
    }

    public TossErrorResponse getErrorResponse() {
        return tossErrorResponse;
    }
}
