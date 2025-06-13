package roomescape.common.exception.impl;

import org.springframework.http.HttpStatus;
import roomescape.common.exception.RoomescapeException;

public class ExternalApiException extends RoomescapeException {

    private final HttpStatus status;

    public ExternalApiException(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
