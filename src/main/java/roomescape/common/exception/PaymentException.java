package roomescape.common.exception;

import java.util.Arrays;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import roomescape.payment.PaymentError;

@Getter
public class PaymentException extends RuntimeException {

    private final HttpStatusCode statusCode;
    private final PaymentError paymentError;

    public PaymentException(final HttpStatusCode statusCode, final PaymentError paymentError) {
        this.statusCode = statusCode;
        this.paymentError = temp(paymentError);
    }

    private PaymentError temp(final PaymentError paymentError) {
        if (!TossErrorCode.exists(paymentError.code())) {
            return paymentError;
        }
        return new PaymentError("서버 에러", "서버 에러");
    }

    enum TossErrorCode {
        INVALID_API_KEY("INTERNAL_SERVER_ERROR", ""),
        UNAUTHORIZED_KEY("", ""),
        INCORRECT_BASIC_AUTH_FORMAT("", "");

        private final String code;

        private final String message;
        TossErrorCode(final String code, final String message) {
            this.code = code;
            this.message = message;
        }

        public static boolean exists(final String code) {
            return Arrays.stream(values())
                    .anyMatch(tossErrorCode -> tossErrorCode.name().equals(code));
        }
    }
}
