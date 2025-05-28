package roomescape.exception.payment;

import java.util.List;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class PaymentException extends RuntimeException {

    private static final List<String> CODE_BLACKLIST = List.of("UNAUTHORIZED_KEY", "INCORRECT_BASIC_AUTH_FORMAT");

    @Getter
    private final HttpStatus status;
    private final String message;

    public PaymentException(final HttpStatusCode statusCode, final String code, final String message) {
        this.status = HttpStatus.valueOf(statusCode.value());
        this.message = resolvePaymentErrorMessage(code, message);
    }

    private String resolvePaymentErrorMessage(final String code, final String message) {
        if (CODE_BLACKLIST.contains(code)) {
            return "오류가 발생하였습니다. 고객센터에 문의해주세요";
        }
        return message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
