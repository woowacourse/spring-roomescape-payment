package roomescape.exception.payment;

import org.springframework.http.HttpStatus;
import roomescape.exception.common.RoomescapeException;

public class PaymentTimeoutException extends RoomescapeException {
    public PaymentTimeoutException(Exception e) {
        super("결제 시도 중 Timeout이 발생했습니다. 다시 시도해주세요.", HttpStatus.GATEWAY_TIMEOUT, e);
    }
}
