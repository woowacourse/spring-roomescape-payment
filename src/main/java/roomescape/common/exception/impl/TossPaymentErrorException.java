package roomescape.common.exception.impl;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.http.HttpStatus;

public class TossPaymentErrorException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public TossPaymentErrorException(final HttpStatus status, final String code, final String message) {
        super(message);
        this.status = status;
        this.code = code;
    }  // NOTE. 토스페이 한정적으로 사용하는 필드라고 현재로써는 생각함. 파랑에게 여쭤보기

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public boolean isTreatedAsServerError() {
        return TossErrorCodesTreatedAsServerError.contains(code);
    }

    public enum TossErrorCodesTreatedAsServerError {
        UNAUTHORIZED_KEY,
        INCORRECT_BASIC_AUTH_FORMAT,
        NOT_FOUND_TERMINAL_ID,
        BELOW_MINIMUM_AMOUNT,
        INVALID_AUTHORIZE_AUTH,
        INVALID_UNREGISTERED_SUBMALL,
        NOT_REGISTERED_BUSINESS;

        private static final Set<String> CODE_SET = Stream.of(values())
            .map(Enum::name)
            .collect(Collectors.toSet());

        public static boolean contains(final String code) {
            return CODE_SET.contains(code);
        }
    }
}
