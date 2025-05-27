package roomescape.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class TossPaymentException extends RuntimeException {

    private final HttpStatusCode status;

    public TossPaymentException(HttpStatusCode status, String message) {
        super(message);
        this.status = status;
    }
}
