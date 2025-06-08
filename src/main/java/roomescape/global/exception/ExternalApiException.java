package roomescape.global.exception;

import org.springframework.http.HttpStatus;
import roomescape.global.dto.ExternalApiErrorResponse;

public class ExternalApiException extends RuntimeException {
    private final ExternalApiErrorResponse errorResponse;

    public ExternalApiException(final ExternalApiErrorResponse errorResponse) {
        super(errorResponse.message());
        this.errorResponse = errorResponse;
    }

    public HttpStatus getStatus() {
        return errorResponse.status();
    }
}
