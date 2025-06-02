package roomescape.reservation.external.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import roomescape.reservation.external.dto.respone.PaymentErrorResponse;

@Getter
public class PaymentClientException extends RuntimeException {

    private final HttpStatusCode httpStatusCode;

    public PaymentClientException(HttpStatusCode statusCode, PaymentErrorResponse response) {
        super(response.message());
        this.httpStatusCode = statusCode;
    }
}
