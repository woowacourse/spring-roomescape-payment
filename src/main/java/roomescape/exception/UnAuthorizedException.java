package roomescape.exception;

import roomescape.exception.type.RoomescapeExceptionType;

public class UnAuthorizedException extends RuntimeException {
    private final RoomescapeExceptionType roomescapeExceptionType;
    private final String logMessage;

    public UnAuthorizedException(RoomescapeExceptionType roomescapeExceptionType) {
        super(roomescapeExceptionType.getMessage());
        this.roomescapeExceptionType = roomescapeExceptionType;
        this.logMessage = roomescapeExceptionType.getLogMessageFormat();
    }

    public UnAuthorizedException(RoomescapeExceptionType roomescapeExceptionType, Object... resource) {
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
