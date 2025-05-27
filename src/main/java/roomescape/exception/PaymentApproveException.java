package roomescape.exception;

public class PaymentApproveException extends RuntimeException{

    private final String code;
    private final String message;

    public PaymentApproveException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
