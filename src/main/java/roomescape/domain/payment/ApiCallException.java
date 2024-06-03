package roomescape.domain.payment;


public class ApiCallException extends RuntimeException {
    private final ApproveApiError apiError;

    public ApiCallException(ApproveApiError apiError) {
        this.apiError = apiError;
    }

    public ApiCallException(String message) {
        super(message);
        this.apiError = null;
    }

    @Override
    public String getMessage() {
        if (apiError != null) {
            return apiError.message();
        }
        return super.getMessage();
    }

    public String getCode() {
        if (apiError != null) {
            return apiError.code();
        }
        return "ERROR_CODE";
    }
}
