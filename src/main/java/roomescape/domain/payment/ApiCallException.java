package roomescape.domain.payment;


public class ApiCallException extends RuntimeException {
    private final ApproveApiError apiError;

    public ApiCallException(ApproveApiError apiError) {
        this.apiError = apiError;
    }

    @Override
    public String getMessage() {
        return apiError.message();
    }
}
