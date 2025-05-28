package roomescape.reservation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import roomescape.reservation.ErrorResponse;

@Getter
public class PaymentServerException extends RuntimeException {

    private final HttpStatusCode httpStatusCode;

    public PaymentServerException(HttpStatusCode httpStatusCode, ErrorResponse response) {
        super(response.message());
        this.httpStatusCode = httpStatusCode;
    }
}
