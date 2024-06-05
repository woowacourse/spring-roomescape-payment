package roomescape.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class PaymentException  extends RuntimeException{
    private HttpStatus httpStatus;
    private String message;

    public PaymentException(HttpStatusCode httpStatusCode, String message) {
        super(message);
        this.httpStatus = HttpStatus.resolve(httpStatusCode.value());
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
