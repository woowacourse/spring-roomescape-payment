package roomescape.reservation.payment.exception;

import java.util.Arrays;

public enum InternalServerErrorCode {
    FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING("결제가 완료되지 않았어요. 다시 시도해주세요."),
    FAILED_INTERNAL_SYSTEM_PROCESSING("내부 시스템 처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요."),
    UNKNOWN_PAYMENT_ERROR("결제에 실패했어요. 같은 문제가 반복된다면 은행이나 카드사로 문의해주세요."),
    UNAUTHORIZED_KEY("인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."),
    INCORRECT_BASIC_AUTH_FORMAT("잘못된 요청입니다. ':' 를 포함해 인코딩해주세요."),
    INVALID_API_KEY("잘못된 시크릿키 연동 정보 입니다.");

    private final String message;

    InternalServerErrorCode(String message) {
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

