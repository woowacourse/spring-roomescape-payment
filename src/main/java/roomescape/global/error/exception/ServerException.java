package roomescape.global.error.exception;

import org.springframework.http.HttpStatus;

public class ServerException extends WarningException {

    public ServerException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
