package roomescape.payment.exception;

import org.springframework.http.HttpStatusCode;

public class PaymentException extends RuntimeException {
    private final HttpStatusCode statusCode;
    private final String response;

    public PaymentException(HttpStatusCode statusCode, String response) {
        this.statusCode = statusCode;
        this.response = response;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public String getResponse() {
        return response;
    }
}
