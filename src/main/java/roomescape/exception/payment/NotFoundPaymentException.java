package roomescape.exception.payment;

import org.springframework.http.HttpStatus;
import roomescape.exception.common.RoomescapeException;

public class NotFoundPaymentException extends RoomescapeException {
    public NotFoundPaymentException() {
        super("존재하지 않는 결제 정보입니다.", HttpStatus.NOT_FOUND);
    }
}
