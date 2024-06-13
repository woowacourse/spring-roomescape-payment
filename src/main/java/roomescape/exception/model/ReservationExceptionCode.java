package roomescape.exception.model;

import org.springframework.http.HttpStatus;
import roomescape.exception.ExceptionCode;

public enum ReservationExceptionCode implements ExceptionCode {

    RESERVATION_TIME_IS_PAST_EXCEPTION(HttpStatus.BAD_REQUEST, "지난 시간의 테마를 선택했습니다."),
    RESERVATION_DATE_IS_PAST_EXCEPTION(HttpStatus.BAD_REQUEST, "지난 날짜의 예약을 시도하였습니다."),
    RESERVATION_NOT_EXIST(HttpStatus.BAD_REQUEST, "예약이 존재하지 않습니다."),
    THEME_INFO_IS_NULL_EXCEPTION(HttpStatus.BAD_REQUEST, "필터링할 테마 정보가 존재하지 않습니다."),
    MEMBER_INFO_IS_NULL_EXCEPTION(HttpStatus.BAD_REQUEST, "필터링할 유저 정보가 존재하지 않습니다."),
    DATE_IS_NULL_EXCEPTION(HttpStatus.BAD_REQUEST, "필터링할 날짜 정보가 존재하지 않습니다."),
    SAME_RESERVATION_EXCEPTION(HttpStatus.BAD_REQUEST, "날짜, 시간, 테마가 같은 예약이 존재합니다."),
    RESERVATION_ALREADY_PAID_CAN_NOT_DELETE_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 결제된 예약은 취소할 수 없습니다."),
    ONLY_OWNER_CAN_DELETE(HttpStatus.FORBIDDEN, "예약을 생성한 사람만 삭제할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ReservationExceptionCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
