package roomescape.exception;

import org.springframework.http.HttpStatusCode;

public class PaymentException extends RoomescapeException {

    public PaymentException(HttpStatusCode code, String message) {
        super(code, message);
    }

}
