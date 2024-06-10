package roomescape.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import roomescape.core.dto.exception.HttpExceptionResponse;

public class PaymentException extends RuntimeException {
    private final HttpStatus httpStatus;

    public PaymentException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }

    public PaymentException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public static PaymentException from(HttpClientErrorException e) {
        HttpExceptionResponse responseBody = e.getResponseBodyAs(HttpExceptionResponse.class);
        if (responseBody == null) {
            return new PaymentException(e.getMessage());
        }
        return from(responseBody);
    }

    private static PaymentException from(HttpExceptionResponse responseBody) {
        String code = responseBody.getCode();
        String message = PaymentApproveErrorCode.getMessageOf(code);

        return new PaymentException(message);
    }

    public HttpStatusCode getStatusCode() {
        return httpStatus;
    }
}
