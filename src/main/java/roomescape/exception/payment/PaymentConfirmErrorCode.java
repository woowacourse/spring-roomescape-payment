package roomescape.exception.payment;

import org.springframework.http.HttpStatus;

import java.util.Arrays;

public enum PaymentConfirmErrorCode {
    ALREADY_PROCESSED_PAYMENT(HttpStatus.BAD_REQUEST, "이미 처리된 결제 입니다."),
    PROVIDER_ERROR(HttpStatus.BAD_REQUEST, "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_API_KEY(HttpStatus.BAD_REQUEST, "잘못된 시크릿키 연동 정보 입니다."),
    INVALID_EXCEED_MAX_DAILY_PAYMENT_COUNT(HttpStatus.BAD_REQUEST, "하루 결제 가능 횟수를 초과했습니다."),
    EXCEED_MAX_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "하루 결제 가능 금액을 초과했습니다."),
    NOT_FOUND_TERMINAL_ID(HttpStatus.BAD_REQUEST, "단말기번호(Terminal Id)가 없습니다. 토스페이먼츠로 문의 바랍니다."),
    UNAPPROVED_ORDER_ID(HttpStatus.BAD_REQUEST, "아직 승인되지 않은 주문번호입니다."),
    UNAUTHORIZED_KEY(HttpStatus.BAD_REQUEST, "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "결제 비밀번호가 일치하지 않습니다."),
    NOT_FOUND_PAYMENT(HttpStatus.BAD_REQUEST, "존재하지 않는 결제 정보 입니다."),
    NOT_FOUND_PAYMENT_SESSION(HttpStatus.BAD_REQUEST, "결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다."),

    INVALID_AUTHORIZE_AUTH(HttpStatus.INTERNAL_SERVER_ERROR, "유효하지 않은 인증 방식입니다."),
    NOT_AVAILABLE_PAYMENT(HttpStatus.INTERNAL_SERVER_ERROR, "결제가 불가능한 시간대입니다"),
    INCORRECT_BASIC_AUTH_FORMAT(HttpStatus.INTERNAL_SERVER_ERROR, "잘못된 요청입니다. ':' 를 포함해 인코딩해주세요."),
    FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING(HttpStatus.INTERNAL_SERVER_ERROR, "결제가 완료되지 않았어요. 다시 시도해주세요."),
    FAILED_INTERNAL_SYSTEM_PROCESSING(HttpStatus.INTERNAL_SERVER_ERROR, "내부 시스템 처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요."),
    UNKNOWN_PAYMENT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "결제에 실패했어요. 같은 문제가 반복된다면 은행이나 카드사로 문의해주세요."),

    PAYMENT_CONFIRM_ERROR_MISMATCH(HttpStatus.INTERNAL_SERVER_ERROR, "결제 과정에서 서버 에러가 발생했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    public static PaymentConfirmErrorCode findByName(String name) {
        return Arrays.stream(values())
                .filter(v -> v.name().equals(name))
                .findAny()
                .orElse(PAYMENT_CONFIRM_ERROR_MISMATCH);
    }

    PaymentConfirmErrorCode(final HttpStatus httpStatus, final String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
