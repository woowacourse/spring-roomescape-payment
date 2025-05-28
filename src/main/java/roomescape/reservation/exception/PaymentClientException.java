package roomescape.reservation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import roomescape.reservation.PaymentErrorResponse;

@Getter
public class PaymentClientException extends RuntimeException {

    private final HttpStatusCode httpStatusCode;

    public PaymentClientException(HttpStatusCode statusCode, PaymentErrorResponse response) {
        super(response.message());
        this.httpStatusCode = statusCode;
    }
}
