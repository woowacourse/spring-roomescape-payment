package roomescape.exception.common;

import org.springframework.http.HttpStatus;

public class BadRequestException extends RuntimeExceptionWithLog {

    public BadRequestException(String message, String logMessage) {
        super(message, "[BAD_REQUEST]" + logMessage, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String message) {
        super(message, "[잘못된 요청] 실패 사유 : " + message, HttpStatus.BAD_REQUEST);
    }
}
