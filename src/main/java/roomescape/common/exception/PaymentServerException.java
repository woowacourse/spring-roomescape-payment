package roomescape.common.exception;

import roomescape.common.exception.error.ErrorCode;
import roomescape.common.exception.error.PaymentErrorCode;

public class PaymentServerException extends CustomException {

    public PaymentServerException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
