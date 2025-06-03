package roomescape.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExternalApiException extends RuntimeException {
    private final String serviceName;
    private final HttpStatus status;
    private final String externalErrorCode;

    public ExternalApiException(String serviceName, HttpStatus status, String externalErrorCode, String message) {
        super(String.format("%s) %s: %s", serviceName, externalErrorCode, message));
        this.serviceName = serviceName;
        this.status = status;
        this.externalErrorCode = externalErrorCode;
    }
}
