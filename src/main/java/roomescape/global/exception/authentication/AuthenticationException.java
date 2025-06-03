package roomescape.global.exception.authentication;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends RuntimeException {

    private final AuthenticationErrorStatus errorStatus;

    public AuthenticationException(AuthenticationErrorStatus errorStatus) {
        super(errorStatus.errorMessage);
        this.errorStatus = errorStatus;
    }

    public HttpStatus getHttpStatus() {
        return errorStatus.httpStatus;
    }
}
