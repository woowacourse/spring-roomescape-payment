package roomescape.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import roomescape.infrastructure.payment.response.PaymentErrorResponse;

public class PaymentException extends RuntimeException {

    private static final HttpStatusCode DEFAULT_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    private final HttpStatusCode statusCode;
    private final PaymentErrorResponse paymentErrorResponse;

    public PaymentException(HttpStatusCode statusCode, PaymentErrorResponse paymentErrorResponse) {
        this(statusCode, paymentErrorResponse, paymentErrorResponse.message());
    }

    public PaymentException(PaymentErrorResponse paymentErrorResponse, String message) {
        this(DEFAULT_STATUS, paymentErrorResponse, message);
    }

    public PaymentException(HttpStatusCode statusCode, PaymentErrorResponse paymentErrorResponse, String message) {
        super(message);
        this.statusCode = statusCode;
        this.paymentErrorResponse = paymentErrorResponse;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public PaymentErrorResponse getPaymentErrorResponse() {
        return paymentErrorResponse;
    }
}
