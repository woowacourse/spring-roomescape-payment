package roomescape.global.exception;

public class PaymentErrorResponse {

    private String code;
    private String message;

    public PaymentErrorResponse() {
    }

    public PaymentErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
