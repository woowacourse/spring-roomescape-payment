package roomescape.payment.application.dto;

public class PaymentErrorResponse {
    private String code;
    private String message;
    private String data;

    public PaymentErrorResponse() {
    }

    public PaymentErrorResponse(final String code, final String message, final String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }
}
