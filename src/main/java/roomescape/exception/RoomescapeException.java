package roomescape.exception;

public class RoomescapeException extends RuntimeException {

    private static final String SERVER_ERROR_MESSAGE = "서버의 문제가 발생했습니다.";

    public RoomescapeException() {
        super(SERVER_ERROR_MESSAGE);
    }
}
