package roomescape.exception.model;

import org.springframework.http.HttpStatus;
import roomescape.exception.ExceptionCode;

public enum PaymentConfirmExceptionCode implements ExceptionCode {

    FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING(HttpStatus.INTERNAL_SERVER_ERROR, "결제가 완료되지 않았어요. 다시 시도해주세요."),
    FOUND_PAYMENT_IS_NULL_EXCEPTION(HttpStatus.BAD_REQUEST, "결제 내역을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    PaymentConfirmExceptionCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
