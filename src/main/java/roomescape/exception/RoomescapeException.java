package roomescape.exception;

import org.springframework.http.HttpStatus;

public class RoomescapeException extends RuntimeException {
    private final RoomescapeExceptionType roomescapeExceptionType;

    public RoomescapeException(RoomescapeExceptionType roomescapeExceptionType) {
        this.roomescapeExceptionType = roomescapeExceptionType;
    }

    @Override
    public String getMessage() {
        return roomescapeExceptionType.getMessage();
    }

    public HttpStatus getHttpStatus() {
        return roomescapeExceptionType.getStatus();
    }
}
