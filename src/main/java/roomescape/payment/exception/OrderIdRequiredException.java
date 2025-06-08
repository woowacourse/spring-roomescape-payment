package roomescape.payment.exception;

import roomescape.common.exception.BusinessException;
import roomescape.common.exception.ErrorCode;

public class OrderIdRequiredException extends BusinessException {
    public OrderIdRequiredException() {
        super(PaymentErrorCode.ORDER_ID_LENGTH_EXCEEDED);
    }
}
