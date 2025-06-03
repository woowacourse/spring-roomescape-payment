package roomescape.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import roomescape.common.exception.base.BusinessException;
import roomescape.payment.dto.PaymentResult;

public class PaymentApiException extends BusinessException {

    private final HttpStatus status;

    public PaymentApiException(final String message, final String errorMessage, final HttpStatusCode statusCode) {
        super(buildLogMessage(message), buildUserMessage(errorMessage));
        this.status = HttpStatus.valueOf(statusCode.value());
    }

    public PaymentApiException(final PaymentResult result) {
        super(buildLogMessage(result), buildUserMessage("서버 내 결제 처리 오류입니다."));
        this.status = HttpStatus.valueOf(500);
    }

    private static String buildLogMessage(final String message) {
        return "결제 승인이 실패하였습니다. " + message;
    }

    private static String buildLogMessage(final PaymentResult result) {
        return "잘못된 결제 요청입니다. " + result.orderId() +
                ", " + result.paymentKey() +
                ", " + result.amount() +
                ", " + result.paymentType();
    }

    private static String buildUserMessage(final String errorMessage) {
        return errorMessage;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return status;
    }
}
