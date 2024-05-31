package roomescape.exception;

import org.springframework.web.client.HttpClientErrorException;
import roomescape.core.dto.exception.HttpExceptionResponse;

public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }

    public static PaymentException from(HttpClientErrorException e) {
        HttpExceptionResponse responseBody = e.getResponseBodyAs(HttpExceptionResponse.class);
        if (responseBody == null) {
            return new PaymentException(e.getMessage());
        }
        return new PaymentException(responseBody.getMessage());
    }
}
