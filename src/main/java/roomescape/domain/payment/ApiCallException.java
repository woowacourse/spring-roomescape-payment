package roomescape.domain.payment;


public class ApiCallException extends RuntimeException {
    private final PaymentApiError apiError;

    public ApiCallException(PaymentApiError apiError) {
        this.apiError = apiError;
    }

    public ApiCallException(PaymentApiError apiError, Throwable cause) {
        super(cause);
        this.apiError = apiError;
    }

    @Override
    public String getMessage() {
        if (apiError != null) {
            return apiError.message();
        }
        return super.getMessage();
    }
}
