package roomescape.exception;

import org.springframework.http.HttpStatus;
import roomescape.infrastructure.payment.TossErrorResponse;

public class TossPaymentException extends RuntimeException {

    private String errorCode;
    private String errorMessage;
    private HttpStatus httpStatus;

    public TossPaymentException(TossPaymentErrorCode TossPaymentErrorCode) {
        this.errorCode = TossPaymentErrorCode.getCode();
        this.errorMessage = TossPaymentErrorCode.getMessage();
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public TossPaymentException(TossPaymentErrorCode TossPaymentErrorCode, HttpStatus httpStatus) {
        this.errorCode = TossPaymentErrorCode.getCode();
        this.errorMessage = TossPaymentErrorCode.getMessage();
        this.httpStatus = httpStatus;
    }

    public TossPaymentException(TossErrorResponse tossErrorResponse, HttpStatus httpStatus) {
        this.errorCode = TossPaymentErrorCode.PAYMENT_FAILED.getCode();
        this.errorMessage = getErrorMessageFrom(tossErrorResponse);
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private String getErrorMessageFrom(TossErrorResponse error) {
        return "토스 결제 실패 : " + error.code() + " (" + error.message() + ")";
    }
}
