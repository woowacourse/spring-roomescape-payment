package roomescape.client;

public class PaymentException extends RuntimeException {
    private final TossErrorResponse tossErrorResponse;
    private final int status;

    public PaymentException(TossErrorResponse tossErrorResponse, int status) {
        super(tossErrorResponse.message());
        this.tossErrorResponse = tossErrorResponse;
        this.status = status;
    }

    public TossErrorResponse getErrorResponse() {
        return tossErrorResponse;
    }

    public int getStatus() {
        return status;
    }
}
