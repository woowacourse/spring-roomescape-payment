package roomescape.exception;

public class RoomescapeException extends RuntimeException {

    private final Exceptions exceptions;

    public RoomescapeException(final Exceptions exceptions) {
        this.exceptions = exceptions;
    }

    public int getHttpStatusCodeValue() {
        return exceptions.getHttpStatusValue();
    }

    @Override
    public String getMessage() {
        return exceptions.getMessage();
    }
}
