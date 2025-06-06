package roomescape.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class SeparatedMessageException extends ApplicationException {

    private final String clientMessage;

    public SeparatedMessageException(HttpStatus status, String message, String clientMessage) {
        super(message, status);
        this.clientMessage = clientMessage;
    }
}
