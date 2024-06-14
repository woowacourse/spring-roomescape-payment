package roomescape.payment.domain;

import java.util.Arrays;

public enum ServerErrorCode {
    INVALID_ORDER_ID("INVALID_ORDER_ID", "orderId는 영문 대소문자, 숫자, 특수문자(-, _) 만 허용합니다. 6자 이상 64자 이하여야 합니다."),
    INVALID_API_KEY("INVALID_API_KEY", "잘못된 시크릿키 연동 정보 입니다."),
    UNAUTHORIZED_KEY("UNAUTHORIZED_KEY", "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."),
    INCORRECT_BASIC_AUTH_FORMAT("INCORRECT_BASIC_AUTH_FORMAT", "잘못된 요청입니다. ':' 를 포함해 인코딩해주세요.");

    private final String code;
    private final String description;

    ServerErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static boolean isServerErrorCode(String code) {
        return Arrays.stream(values())
                .anyMatch(serverErrorCode -> code.equals(serverErrorCode.code));
    }
}
