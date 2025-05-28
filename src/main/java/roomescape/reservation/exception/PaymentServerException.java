package roomescape.reservation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import roomescape.reservation.PaymentErrorResponse;

@Getter
public class PaymentServerException extends RuntimeException {

    private final HttpStatusCode httpStatusCode;

    public PaymentServerException(HttpStatusCode httpStatusCode, PaymentErrorResponse response) {
        super(response.message());
        this.httpStatusCode = httpStatusCode;
    }
}
