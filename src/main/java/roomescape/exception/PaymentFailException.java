package roomescape.exception;

public class PaymentFailException extends RuntimeException {
    private final String code;

    public PaymentFailException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
