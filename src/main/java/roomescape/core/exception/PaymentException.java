package roomescape.core.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import roomescape.core.dto.exception.HttpExceptionResponse;

public class PaymentException extends HttpClientErrorException {
    private final HttpExceptionResponse responseBody;

    public PaymentException(final HttpStatusCode statusCode, final String statusText,
                            final HttpExceptionResponse response) {
        super(statusCode, statusText);
        this.responseBody = response;
    }

    public HttpExceptionResponse getResponseBody() {
        return responseBody;
    }
}
