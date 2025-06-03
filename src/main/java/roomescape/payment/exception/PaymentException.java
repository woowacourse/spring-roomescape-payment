package roomescape.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import roomescape.common.exception.base.BusinessException;

public class PaymentException extends BusinessException {

    private final HttpStatus status;

    public PaymentException(final String message, final String errorMessage, final HttpStatusCode statusCode) {
        super(buildLogMessage(message), buildUserMessage(errorMessage));
        this.status = HttpStatus.valueOf(statusCode.value());
    }

    private static String buildLogMessage(final String message) {
        return "결제 승인 API 호출 실패했습니다. " + message;
    }

    private static String buildUserMessage(final String message) {
        return message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return status;
    }
}
