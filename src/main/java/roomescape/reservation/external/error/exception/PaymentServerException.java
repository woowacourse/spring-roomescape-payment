package roomescape.reservation.external.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import roomescape.reservation.external.dto.respone.PaymentErrorResponse;

@Getter
public class PaymentServerException extends RuntimeException {

    private final HttpStatusCode httpStatusCode;

    public PaymentServerException(HttpStatusCode httpStatusCode, PaymentErrorResponse response) {
        super(response.message());
        this.httpStatusCode = httpStatusCode;
    }
}
