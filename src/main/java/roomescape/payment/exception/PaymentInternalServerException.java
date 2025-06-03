package roomescape.payment.exception;

import org.springframework.http.HttpStatus;
import roomescape.common.exception.base.BusinessException;

public class PaymentInternalServerException extends BusinessException {

    public PaymentInternalServerException(final String errorMessage, final String message) {
        super(buildLogMessage(errorMessage), buildUserMessage(message));
    }

    public PaymentInternalServerException(final String errorMessage) {
        super(buildLogMessage(errorMessage), "결제 처리 중 오류가 발생했습니다. 관리자에게 문의해주세요.");
    }

    private static String buildLogMessage(final String message) {
        return "결제 승인 API 호출 실패했습니다. " + message;
    }

    private static String buildUserMessage(final String message) {
        return message + "관리자에게 문의하세요.";
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
