package roomescape.exception.model;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import roomescape.exception.ExceptionCode;

@Tag(name = "예약 대기 예외 코드", description = "예약 대기 과정에서 발생할 수 있는 예외 코드 모음")
public enum WaitingExceptionCode implements ExceptionCode {

    WAITING_NOT_EXIST_EXCEPTION(HttpStatus.BAD_REQUEST, "예약 대기가 존재하지 않습니다."),
    WAITING_TIME_IS_PAST_EXCEPTION(HttpStatus.BAD_REQUEST, "지난 시간의 테마를 선택했습니다."),
    WAITING_DATE_IS_PAST_EXCEPTION(HttpStatus.BAD_REQUEST, "지난 날짜의 예약을 시도하였습니다."),
    THEME_INFO_IS_NULL_EXCEPTION(HttpStatus.BAD_REQUEST, "필터링할 테마 정보가 존재하지 않습니다."),
    MEMBER_INFO_IS_NULL_EXCEPTION(HttpStatus.BAD_REQUEST, "필터링할 유저 정보가 존재하지 않습니다."),
    DATE_IS_NULL_EXCEPTION(HttpStatus.BAD_REQUEST, "필터링할 날짜 정보가 존재하지 않습니다."),
    ALREADY_REGISTRATION_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 예약 혹은 예약 대기를 신청하였습니다."),
    APPROVE_ORDER_EXCEPTION(HttpStatus.BAD_REQUEST, "예약 대기 순서대로 승인해야 합니다.");;

    private final HttpStatus httpStatus;
    private final String message;

    WaitingExceptionCode(HttpStatus httpStatus, String message) {
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
