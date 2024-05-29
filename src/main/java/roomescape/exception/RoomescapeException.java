package roomescape.exception;

import org.springframework.http.HttpStatusCode;

public class RoomescapeException extends RuntimeException {

    private final HttpStatusCode code;
    private final String message;

    public RoomescapeException(HttpStatusCode code, String message) {
        this.code = code;
        this.message = message;
    }

    public HttpStatusCode getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
