package roomescape.exception;

import org.springframework.http.HttpStatusCode;

public class PaymentException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public PaymentException(PaymentErrorMessage paymentErrorMessage) {
        super(paymentErrorMessage.getErrorMessageKorean());
        this.statusCode = paymentErrorMessage.getStatusCode();
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
