package roomescape.exception;

import org.springframework.http.HttpStatusCode;

public interface ErrorCodeWithHttpStatusCode {
    HttpStatusCode httpStatusCode();

    String message();
}
