package roomescape.paymenthistory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class PaymentException extends RuntimeException {

    private final HttpStatusCode httpStatus;

    public PaymentException(String message, HttpStatusCode httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public static class PaymentServerError extends PaymentException {
        public PaymentServerError() {
            super("내부 서버 에러가 발생했습니다. 관리자에게 문의해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatus;
    }
}
