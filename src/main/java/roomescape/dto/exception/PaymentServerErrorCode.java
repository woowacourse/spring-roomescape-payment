package roomescape.dto.exception;

import java.util.Arrays;

public enum PaymentServerErrorCode {
    UNAUTHORIZED_KEY("인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."),
    INCORRECT_BASIC_AUTH_FORMAT("잘못된 요청입니다. ':' 를 포함해 인코딩해주세요."),
    INVALID_API_KEY("잘못된 시크릿키 연동 정보 입니다."),
    ;

    private final String message;

    PaymentServerErrorCode(String message) {
        this.message = message;
    }

    public static boolean contains(final String errorCode) {
        return Arrays.stream(values())
                .anyMatch(value -> value.name().equals(errorCode));
    }

    public String getMessage() {
        return message;
    }
}
