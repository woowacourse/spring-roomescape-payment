package roomescape.exception;

import roomescape.exception.type.RoomescapeExceptionType;

public class RoomescapeException extends RuntimeException {
    private final RoomescapeExceptionType roomescapeExceptionType;
    private final String logMessage;

    public RoomescapeException(RoomescapeExceptionType roomescapeExceptionType) {
        super(roomescapeExceptionType.getMessage());
        this.roomescapeExceptionType = roomescapeExceptionType;
        this.logMessage = roomescapeExceptionType.getLogMessage();
    }

    public RoomescapeException(RoomescapeExceptionType roomescapeExceptionType, Object... resource) {
        super(roomescapeExceptionType.getMessage());
        this.roomescapeExceptionType = roomescapeExceptionType;
        this.logMessage = String.format(roomescapeExceptionType.getLogMessage(), resource);
    }

    @Override
    public String getMessage() {
        return roomescapeExceptionType.getMessage();
    }

    public RoomescapeExceptionType getExceptionType() {
        return roomescapeExceptionType;
    }

    public String getLogMessage() {
        return logMessage;
    }
}
