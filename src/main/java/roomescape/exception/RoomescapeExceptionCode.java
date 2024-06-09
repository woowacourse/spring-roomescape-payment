package roomescape.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum RoomescapeExceptionCode {

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다."),
    RESERVATION_TIME_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 예약 시간입니다."),
    WAITING_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 예약 대기입니다."),
    THEME_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 테마입니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 결제 건입니다."),
    WAITING_FOR_NO_RESERVATION(HttpStatus.BAD_REQUEST, "예약이 없는 건에는 예약 대기를 할 수 없습니다."),
    WAITING_FOR_MY_RESERVATION(HttpStatus.BAD_REQUEST, "이미 예약한 건에는 예약 대기를 할 수 없습니다."),
    WAITING_DUPLICATED(HttpStatus.BAD_REQUEST, "중복된 예약 대기를 할 수 없습니다."),
    CANNOT_REJECT_WAITING(HttpStatus.BAD_REQUEST, "확정된 예약 건은 거절할 수 없습니다."),
    INVALID_DATE(HttpStatus.BAD_REQUEST, "이전 날짜 혹은 당일은 예약할 수 없습니다."),
    RESERVATION_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "해당 시간에 예약이 찼습니다."),
    EMPTY_NAME(HttpStatus.BAD_REQUEST, "예약자 이름은 비어있을 수 없습니다."),
    EMPTY_TIME(HttpStatus.BAD_REQUEST, "예약 시간이 비어 있습니다."),
    INVALID_NAME_FORMAT(HttpStatus.BAD_REQUEST, "예약자 이름은 숫자로만 구성될 수 없습니다."),
    INVALID_TIME_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 예약 시간입니다."),
    TOSS_PAYMENTS_ERROR(HttpStatus.BAD_REQUEST, "결제에 실패했습니다."),
    DATABASE_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "결제 진행 중 예기치 못한 에러가 발생했습니다.")
    ;

    private final HttpStatusCode httpStatusCode;
    private final String message;

    RoomescapeExceptionCode(HttpStatusCode httpStatusCode, String message) {
        this.httpStatusCode = httpStatusCode;
        this.message = message;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getMessage() {
        return message;
    }
}
