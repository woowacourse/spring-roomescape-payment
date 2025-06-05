package roomescape.payment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class PaymentServerException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "일시적으로 결제 서비스를 이용할 수 없습니다. 잠시 후 다시 시도해 주세요.";

    private final HttpStatusCode statusCode;

    public PaymentServerException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public PaymentServerException(HttpStatusCode statusCode) {
        this(DEFAULT_MESSAGE, statusCode);
    }
}
