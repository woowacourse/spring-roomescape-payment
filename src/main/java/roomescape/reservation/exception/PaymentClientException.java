package roomescape.reservation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import roomescape.reservation.ErrorResponse;

@Getter
public class PaymentClientException extends RuntimeException {

    private final HttpStatusCode httpStatusCode;

    public PaymentClientException(HttpStatusCode statusCode, ErrorResponse response) {
        super(response.message());
        this.httpStatusCode = statusCode;
    }
}
