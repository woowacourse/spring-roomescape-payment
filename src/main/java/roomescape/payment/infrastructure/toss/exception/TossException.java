package roomescape.payment.infrastructure.toss.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TossException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    public TossException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}
