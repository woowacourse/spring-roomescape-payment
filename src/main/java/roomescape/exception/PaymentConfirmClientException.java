package roomescape.exception;

import roomescape.client.PaymentErrorResponse;

public class PaymentConfirmClientException extends RuntimeException {

    private final PaymentErrorResponse paymentErrorResponse;

    public PaymentConfirmClientException(PaymentErrorResponse paymentErrorResponse) {
        super(paymentErrorResponse.getMessage());
        this.paymentErrorResponse = paymentErrorResponse;
    }

    public PaymentErrorResponse getPaymentErrorResponse() {
        return paymentErrorResponse;
    }
}
