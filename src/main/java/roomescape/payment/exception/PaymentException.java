package roomescape.payment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class PaymentException extends RuntimeException {
    private final HttpStatusCode status;

    public PaymentException(final HttpStatusCode status, final String message) {
        super(message);
        this.status = status;
    }
}