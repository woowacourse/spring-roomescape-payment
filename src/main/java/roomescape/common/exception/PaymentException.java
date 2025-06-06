package roomescape.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class PaymentException extends RuntimeException {

    private final HttpStatusCode statusCode;
    private final String code;

    public PaymentException(final HttpStatusCode statusCode, final String message, final String code) {
        super(message);
        this.statusCode = statusCode;
        this.code = code;
    }

    public PaymentException(final HttpStatus status, final String message, final Throwable cause) {
        super(message, cause);
        this.statusCode = HttpStatusCode.valueOf(status.value());
        this.code = status.name();
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public String getCode() {
        return code;
    }
}
