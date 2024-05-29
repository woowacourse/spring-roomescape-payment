package roomescape.exception;

import org.springframework.http.HttpStatusCode;
import roomescape.dto.PaymentErrorResponse;

public class PaymentException extends RuntimeException {

    private final PaymentErrorResponse paymentErrorResponse;
    private final HttpStatusCode httpStatusCode;

    public PaymentException(PaymentErrorResponse paymentErrorResponse, HttpStatusCode httpStatusCode) {
        this.paymentErrorResponse = paymentErrorResponse;
        this.httpStatusCode = httpStatusCode;
    }

    public PaymentErrorResponse getPaymentErrorResponse() {
        return paymentErrorResponse;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }
}
