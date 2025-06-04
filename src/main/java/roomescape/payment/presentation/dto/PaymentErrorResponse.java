package roomescape.payment.presentation.dto;

public class PaymentErrorResponse {
    private String code;
    private String message;

    private PaymentErrorResponse() {
    }

    public PaymentErrorResponse(final String code, final String message) {
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
