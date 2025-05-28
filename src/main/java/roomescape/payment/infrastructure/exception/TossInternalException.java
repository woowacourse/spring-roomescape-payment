package roomescape.payment.infrastructure.exception;

import org.springframework.http.HttpStatus;

public class TossInternalException extends TossException {
    public TossInternalException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "결제 승인에 오류가 발생하였습니다. 관리자에게 문의하세요");
    }
}
