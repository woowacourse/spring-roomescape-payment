package roomescape.payment.exception;

import org.springframework.http.HttpStatus;
import roomescape.common.exception.base.BusinessException;

public class PaymentApiUnauthorizedException extends BusinessException {

    public PaymentApiUnauthorizedException(final String message) {
        super(buildLogMessage(message), buildUserMessage());
    }

    private static String buildLogMessage(final String message) {
        return "결제 인가/인증이 실패하였습니다. " + message;
    }

    private static String buildUserMessage() {
        return "서버 내부 오류가 발생했습니다.";
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
