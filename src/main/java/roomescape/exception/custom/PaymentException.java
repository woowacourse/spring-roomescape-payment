package roomescape.exception.custom;

import org.springframework.http.HttpStatusCode;
import roomescape.dto.payment.PaymentRequest;

public class PaymentException extends RuntimeException {

    private final HttpStatusCode clientStatusCode;
    private final PaymentRequest paymentRequest;
    private final Exception exception;

    public PaymentException(final Exception exception,
                            final HttpStatusCode clientStatusCode,
                            final String errorMessage,
                            final PaymentRequest paymentRequest) {
        super(errorMessage);
        this.exception = exception;
        this.clientStatusCode = clientStatusCode;
        this.paymentRequest = paymentRequest;
    }

    public PaymentRequest getPaymentRequest() {
        return paymentRequest;
    }

    public HttpStatusCode getClientStatusCode() {
        return clientStatusCode;
    }

    public Exception getException() {
        return exception;
    }
}
