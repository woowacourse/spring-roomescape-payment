package roomescape.reservation.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import roomescape.reservation.dto.response.PaymentErrorResponse;

@Getter
public class PaymentClientException extends RuntimeException {

    private final HttpStatusCode httpStatusCode;

    public PaymentClientException(HttpStatusCode statusCode, PaymentErrorResponse response) {
        super(response.message());
        this.httpStatusCode = statusCode;
    }
}
