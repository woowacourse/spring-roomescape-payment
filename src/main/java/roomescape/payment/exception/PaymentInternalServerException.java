package roomescape.payment.exception;

import org.springframework.http.HttpStatus;
import roomescape.common.exception.base.BusinessException;

public class PaymentInternalServerException extends BusinessException {


    public PaymentInternalServerException(final String errorMessage, String message) {
        super(buildLogMessage(errorMessage), buildUserMessage(message));
    }

    private static String buildLogMessage(final String message) {
        return "Payment 결제 승인 API 호출 실패했습니다. " + message;
    }

    private static String buildUserMessage(final String message) {
        return message + "관리자에게 문의하세요.";
    }


    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
