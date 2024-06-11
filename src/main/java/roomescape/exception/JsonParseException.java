package roomescape.exception;

import org.springframework.http.HttpStatus;

public class JsonParseException extends CustomException {
    public JsonParseException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
