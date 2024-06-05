package roomescape.exception.payment;

import org.springframework.http.HttpStatus;
import roomescape.exception.common.RoomescapeException;

public class PaymentClientTimeoutException extends RoomescapeException {
    public PaymentClientTimeoutException() {
        super("시간이 초과되어 결제 요청에 실패했습니다. 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
