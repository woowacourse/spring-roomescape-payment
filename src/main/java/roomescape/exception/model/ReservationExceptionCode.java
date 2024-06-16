package roomescape.exception.model;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import roomescape.exception.ExceptionCode;

@Tag(name = "예약 예외 코드", description = "예약 도중 발생할 수 있는 예외 코드 모음")
public enum ReservationExceptionCode implements ExceptionCode {

    RESERVATION_TIME_IS_PAST_EXCEPTION(HttpStatus.BAD_REQUEST, "지난 시간의 테마를 선택했습니다."),
    RESERVATION_DATE_IS_PAST_EXCEPTION(HttpStatus.BAD_REQUEST, "지난 날짜의 예약을 시도하였습니다."),
    RESERVATION_NOT_EXIST(HttpStatus.BAD_REQUEST, "예약이 존재하지 않습니다."),
    THEME_INFO_IS_NULL_EXCEPTION(HttpStatus.BAD_REQUEST, "필터링할 테마 정보가 존재하지 않습니다."),
    MEMBER_INFO_IS_NULL_EXCEPTION(HttpStatus.BAD_REQUEST, "필터링할 유저 정보가 존재하지 않습니다."),
    DATE_IS_NULL_EXCEPTION(HttpStatus.BAD_REQUEST, "필터링할 날짜 정보가 존재하지 않습니다."),
    SAME_RESERVATION_EXCEPTION(HttpStatus.BAD_REQUEST, "날짜, 시간, 테마가 같은 예약이 존재합니다."),
    RESERVATION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "서버 문제로 예약에 실패하였습니다."),
    RESERVATION_URI_MOVE(HttpStatus.FOUND, "예약 주소가 바뀌었습니다. 새로운 예약 주소로 다시 시도해 주세요.");

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
