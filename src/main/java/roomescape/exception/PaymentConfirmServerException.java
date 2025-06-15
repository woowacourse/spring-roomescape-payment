package roomescape.exception;

import roomescape.exception.common.InternalServerException;

public class PaymentConfirmServerException extends InternalServerException {

    public PaymentConfirmServerException(String message, String code) {
        super("결제에 실패했습니다. 잠시 후 다시 시도해주세요.",
                String.format("[결제 도중 에러 발생] %s[%s]", code, message));
    }
}
