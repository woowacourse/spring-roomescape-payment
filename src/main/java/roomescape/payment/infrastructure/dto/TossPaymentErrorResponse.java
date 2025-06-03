package roomescape.payment.infrastructure.dto;

public class TossPaymentErrorResponse {
    private String code;
    private String message;
    private String data;

    public TossPaymentErrorResponse() {
    }

    public TossPaymentErrorResponse(final String code, final String message, final String data) {
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
