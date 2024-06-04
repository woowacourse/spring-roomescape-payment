package roomescape.exception;

import roomescape.exception.type.RoomescapeExceptionType;

public class ForbiddenException extends RuntimeException {
    private final RoomescapeExceptionType roomescapeExceptionType;
    private final String logMessage;

    public ForbiddenException(RoomescapeExceptionType roomescapeExceptionType) {
        super(roomescapeExceptionType.getMessage());
        this.roomescapeExceptionType = roomescapeExceptionType;
        this.logMessage = roomescapeExceptionType.getLogMessageFormat();
    }

    public ForbiddenException(RoomescapeExceptionType roomescapeExceptionType, Object... resource) {
        super(roomescapeExceptionType.getMessage());
        this.roomescapeExceptionType = roomescapeExceptionType;
        this.logMessage = String.format(roomescapeExceptionType.getLogMessageFormat(), resource);
    }

    @Override
    public String getMessage() {
        return roomescapeExceptionType.getMessage();
    }

    public RoomescapeExceptionType getRoomescapeExceptionType() {
        return roomescapeExceptionType;
    }

    public String getLogMessage() {
        return logMessage;
    }
}
