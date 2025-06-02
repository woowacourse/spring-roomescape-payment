package roomescape.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import roomescape.common.exception.base.BusinessException;

public class PaymentApiException extends BusinessException {

    private final HttpStatus status;

    public PaymentApiException(final String message, final String errorMessage, final HttpStatusCode statusCode) {
        super(buildLogMessage(message), buildUserMessage(errorMessage));
        this.status = HttpStatus.valueOf(statusCode.value());
    }

    private static String buildLogMessage(final String message) {
        return "결제 Api가 실패하였습니다. " + message;
    }

    private static String buildUserMessage(final String errorMessage) {
        return errorMessage;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return status;
    }
}
