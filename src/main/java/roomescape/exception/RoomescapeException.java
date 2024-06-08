package roomescape.exception;

import org.springframework.http.HttpStatus;

public class RoomescapeException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final RoomescapeExceptionResponse roomescapeExceptionResponse;

    public RoomescapeException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.roomescapeExceptionResponse = new RoomescapeExceptionResponse(message);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public RoomescapeExceptionResponse getRoomescapeExceptionResponse() {
        return roomescapeExceptionResponse;
    }
}
