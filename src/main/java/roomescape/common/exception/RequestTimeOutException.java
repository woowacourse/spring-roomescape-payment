package roomescape.common.exception;

public class RequestTimeOutException extends RuntimeException {

    public RequestTimeOutException() {
        super("요청 시간이 초과되었습니다.");
    }
}
