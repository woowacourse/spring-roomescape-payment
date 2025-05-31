package roomescape.payment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class TossPaymentException extends RuntimeException {
    private final HttpStatusCode code;
    private final boolean isServerError;

    public TossPaymentException(HttpStatusCode code, String message, boolean isServerError) {
        super(message);
        this.code = code;
        this.isServerError = isServerError;
    }
}
