package roomescape.exception;

import java.util.Optional;
import org.springframework.web.client.HttpClientErrorException;
import roomescape.core.dto.exception.HttpExceptionResponse;

public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }

    public static PaymentException from(HttpClientErrorException e) {
        final String message = Optional.ofNullable(e.getResponseBodyAs(HttpExceptionResponse.class))
                .map(HttpExceptionResponse::getMessage)
                .orElse(e.getMessage());

        return new PaymentException(message);
    }
}
