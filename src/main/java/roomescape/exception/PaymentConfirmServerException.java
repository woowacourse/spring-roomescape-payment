package roomescape.exception;

import roomescape.client.PaymentErrorResponse;

public class PaymentConfirmServerException extends RuntimeException {

    private final PaymentErrorResponse paymentErrorResponse;

    public PaymentConfirmServerException(PaymentErrorResponse paymentErrorResponse) {
        super(paymentErrorResponse.getMessage());
        this.paymentErrorResponse = paymentErrorResponse;
    }

    public PaymentErrorResponse getPaymentErrorResponse() {
        return paymentErrorResponse;
    }
}
