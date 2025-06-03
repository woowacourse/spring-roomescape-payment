package roomescape.exception.custom.status;

import org.springframework.http.HttpStatus;

public class ServerInternalException extends CustomException {

    public ServerInternalException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");
    }
}
