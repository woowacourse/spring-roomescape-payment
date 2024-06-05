package roomescape.exception.payment;

import org.springframework.http.HttpStatus;
import roomescape.exception.common.RoomescapeException;

public class PaymentConfirmException extends RoomescapeException {

    public PaymentConfirmException(PaymentConfirmErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode.getHttpStatus());
    }

    public PaymentConfirmException(Throwable throwable) {
        super("결제 과정에서 서버 에러가 발생했습니다. 관리자에게 문의해주세요.", HttpStatus.INTERNAL_SERVER_ERROR, throwable);
    }
}
