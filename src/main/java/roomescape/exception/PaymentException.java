package roomescape.exception;

import org.springframework.http.HttpStatusCode;

public class PaymentException extends RuntimeException {

    private final String exceptionClass;
    private final HttpStatusCode serverStatusCode;
    private final String paymentKey;
    private final HttpStatusCode clientStatusCode;

    public PaymentException(final String exceptionClass,
                            final HttpStatusCode serverStatusCode,
                            final HttpStatusCode clientStatusCode,
                            final String message,
                            final String paymentKey
                            ) {
        super(message);
        this.exceptionClass = exceptionClass;
        this.serverStatusCode = serverStatusCode;
        this.clientStatusCode = clientStatusCode;
        this.paymentKey = paymentKey;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public HttpStatusCode getServerStatusCode() {
        return serverStatusCode;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public HttpStatusCode getClientStatusCode() {
        return clientStatusCode;
    }
}
