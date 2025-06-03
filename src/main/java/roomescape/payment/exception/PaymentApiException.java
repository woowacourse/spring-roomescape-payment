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
        super(buildLogMessage(result), buildUserMessage("결제 승인 요청에 실패했습니다. 관리자에게 문의하세요."));
        this.status = HttpStatus.valueOf(500);
    }

    public PaymentApiException() {
        super(buildLogMessage(""), buildUserMessage("결제 승인 요청에 실패했습니다. 관리자에게 문의하세요."));
        this.status = HttpStatus.valueOf(500);
    }

    private static String buildLogMessage(final String message) {
        return "Payment 결제 승인 API 호출 실패했습니다. " + message;
    }

    private static String buildLogMessage(final PaymentResult result) {
        return "Payment 결제 승인 API 호출 실패했습니다. " + result.orderId() +
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
