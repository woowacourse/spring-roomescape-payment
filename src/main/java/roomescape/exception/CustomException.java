package roomescape.exception;

import org.springframework.http.HttpStatusCode;

public class CustomException extends RuntimeException {

    private final HttpStatusCode status;

    public CustomException(String message, HttpStatusCode status) {
        super(message);
        this.status = status;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public HttpStatusCode getStatus() {
        return status;
    }
}
