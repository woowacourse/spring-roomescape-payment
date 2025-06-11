package roomescape.payment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class InvalidPaymentException extends PaymentException {

    private static final String DEFAULT_MESSAGE = "유효하지 않는 결제 요청입니다.";

    private final HttpStatusCode statusCode;

    public InvalidPaymentException(String message, HttpStatusCode statusCode) {
        super(message, statusCode);
        this.statusCode = statusCode;
    }

    public InvalidPaymentException(HttpStatusCode statusCode) {
        this(DEFAULT_MESSAGE, statusCode);
    }
}
