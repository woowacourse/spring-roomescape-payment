package roomescape.payment.exception;

import roomescape.payment.service.dto.PaymentErrorResponse;

public class PaymentException extends RuntimeException {
    private final String code;
    private final String message;


    public PaymentException(PaymentErrorResponse paymentErrorResponse) {
        super(paymentErrorResponse.message());
        this.code = paymentErrorResponse.code();
        this.message = paymentErrorResponse.message();
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
