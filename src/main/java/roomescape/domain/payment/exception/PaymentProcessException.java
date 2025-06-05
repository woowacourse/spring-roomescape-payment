package roomescape.domain.payment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class PaymentProcessException extends RuntimeException {
    private final HttpStatusCode status;

    public PaymentProcessException(final HttpStatusCode status, final String message) {
        super(message);
        this.status = status;
    }
}