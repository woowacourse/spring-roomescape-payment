package roomescape.payment.presentation.dto;

public class PaymentErrorResponse {
    private String code;
    private String message;
    private String orderId;

    private PaymentErrorResponse() {
    }

    public PaymentErrorResponse(final String code, final String message, final String orderId) {
        this.code = code;
        this.message = message;
        this.orderId = orderId;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getOrderId() {
        return orderId;
    }
}
