package roomescape.exception.payment;

import org.springframework.http.HttpStatus;
import roomescape.exception.common.RoomescapeException;

public class InvalidPaymentMethodException extends RoomescapeException {
    public InvalidPaymentMethodException() {
        super("존재하지 않는 결제 방식입니다.", HttpStatus.BAD_REQUEST);
    }
}
