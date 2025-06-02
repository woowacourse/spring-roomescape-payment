package roomescape.global.exception.roomescape;

import org.springframework.http.HttpStatus;

public class RoomEscapeException extends RuntimeException {

    private final RoomEscapeErrorStatus roomEscapeErrorStatus;

    public RoomEscapeException(RoomEscapeErrorStatus roomEscapeErrorStatus) {
        super(roomEscapeErrorStatus.errorMessage);
        this.roomEscapeErrorStatus = roomEscapeErrorStatus;
    }

    public HttpStatus getHttpStatus() {
        return roomEscapeErrorStatus.httpStatus;
    }
}
