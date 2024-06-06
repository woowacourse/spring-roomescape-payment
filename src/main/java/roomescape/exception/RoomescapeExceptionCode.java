package roomescape.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum RoomescapeExceptionCode implements RoomescapeErrorCode {

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다."),
    RESERVATION_TIME_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 예약 시간입니다."),
    WAITING_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 예약 대기입니다."),
    THEME_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 테마입니다."),
    WAITING_FOR_NO_RESERVATION(HttpStatus.BAD_REQUEST, "예약이 없는 건에는 예약 대기를 할 수 없습니다."),
    WAITING_FOR_MY_RESERVATION(HttpStatus.BAD_REQUEST, "이미 예약한 건에는 예약 대기를 할 수 없습니다."),
    WAITING_DUPLICATED(HttpStatus.BAD_REQUEST, "중복된 예약 대기를 할 수 없습니다."),
    INVALID_DATE(HttpStatus.BAD_REQUEST, "이전 날짜 혹은 당일은 예약할 수 없습니다."),
    RESERVATION_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "해당 시간에 예약이 찼습니다."),
    EMPTY_NAME(HttpStatus.BAD_REQUEST, "예약자 이름은 비어있을 수 없습니다."),
    EMPTY_TIME(HttpStatus.BAD_REQUEST, "예약 시간이 비어 있습니다."),
    EMPTY_DATE(HttpStatus.BAD_REQUEST, "예약 날짜가 비어있습니다."),
    INVALID_NAME_FORMAT(HttpStatus.BAD_REQUEST, "예약자 이름은 숫자로만 구성될 수 없습니다."),
    INVALID_TIME_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 예약 시간입니다."),
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 예약 날짜입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "예상하지 못한 오류가 발생했습니다.")
    ;

    private final HttpStatusCode httpStatusCode;
    private final String message;

    RoomescapeExceptionCode(HttpStatusCode httpStatusCode, String message) {
        this.httpStatusCode = httpStatusCode;
        this.message = message;
    }

    @Override
    public HttpStatusCode httpStatusCode() {
        return httpStatusCode;
    }

    @Override
    public String message() {
        return message;
    }
}
