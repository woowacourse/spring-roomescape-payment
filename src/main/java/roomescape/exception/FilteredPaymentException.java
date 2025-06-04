package roomescape.exception;

import org.springframework.http.HttpStatus;

public class FilteredPaymentException extends CustomException {
    public FilteredPaymentException() {
        super("결제가 실패했습니다. 고객센터로 문의해 주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
