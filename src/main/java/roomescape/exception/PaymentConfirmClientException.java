package roomescape.exception;

import roomescape.exception.common.BadRequestException;

public class PaymentConfirmClientException extends BadRequestException {

    public PaymentConfirmClientException(String message, String code) {
        super("결제 도중 문제가 발생했습니다. 결제 카드사로 문의해주세요.",
                String.format("[결제 도중 에러 발생] %s[%s]", code, message));
    }
}
