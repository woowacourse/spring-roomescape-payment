package roomescape.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "시스템에서 오류가 발생했습니다. 관리자에게 문의해주세요."),
    NOT_VALIDATE_ACCESS_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),
    EXTERNAL_API_TIMEOUT(HttpStatus.INTERNAL_SERVER_ERROR, "결제 요청이 지연되고 있습니다. 잠시후 다시 시도해주세요."),

    HTTP_BODY_NOT_READABLE(HttpStatus.BAD_REQUEST, "요청 본문을 읽을 수 없습니다. 요청 형식을 확인해 주세요."),

    USER_NOT_FOUND_BY_ID(HttpStatus.BAD_REQUEST, "잘못된 회원 정보 입니다."),
    USER_NOT_FOUND_BY_EMAIL(HttpStatus.BAD_REQUEST, "등록된 아이디가 아닙니다."),
    USER_NOT_MATCH_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 비밀번호 입니다."),

    RESERVATION_NOT_FOUND_BY_ID(HttpStatus.BAD_REQUEST, "잘못된 예약 정보 입니다."),
    RESERVATION_NOT_REGISTER_BY_PAST_DATE(HttpStatus.BAD_REQUEST, "지나간 날짜와 시간은 예약이 불가능합니다."),
    RESERVATION_NOT_REGISTER_BY_DUPLICATE(HttpStatus.BAD_REQUEST, "해당 시간에 동일한 테마가 예약되어있어 예약이 불가능합니다."),
    RESERVATION_NOT_WAITING_BY_PAST_DATE(HttpStatus.BAD_REQUEST, "지나간 날짜와 시간은 대기가 불가능합니다."),
    RESERVATION_NOT_DELETE_BY_PAST_DATE(HttpStatus.BAD_REQUEST, "지나간 날짜와 시간은 취소가 불가능합니다."),
    RESERVATION_NOT_RESERVED_STATUS(HttpStatus.BAD_REQUEST, "예약 완료 상태의 예약이 아닙니다."),
    RESERVATION_NOT_PAYMENT_PENDING_STATUS(HttpStatus.BAD_REQUEST, "결제 대기 상태의 예약이 아닙니다."),
    RESERVATION_NOT_WAITING_STATUS(HttpStatus.BAD_REQUEST, "예약 대기 상태의 예약이 아닙니다."),
    RESERVATION_ALREADY_PAYMENT(HttpStatus.BAD_REQUEST, "이미 결제된 예약입니다."),

    TIME_NOT_FOUND_BY_ID(HttpStatus.BAD_REQUEST, "잘못된 예약시간 정보 입니다."),
    TIME_NOT_REGISTER_BY_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 등록된 시간은 등록할 수 없습니다."),
    TIME_NOT_DELETE_BY_EXIST_TIME(HttpStatus.BAD_REQUEST, "해당 시간에 예약이 존재해서 삭제할 수 없습니다."),

    THEME_NOT_FOUND_BY_ID(HttpStatus.BAD_REQUEST, "잘못된 테마 정보 입니다."),
    THEME_NOT_REGISTER_BY_DUPLICATE(HttpStatus.BAD_REQUEST, "동일한 이름의 테마가 존재해 등록할 수 없습니다."),
    THEME_NOT_DELETE_BY_DUPLICATE(HttpStatus.BAD_REQUEST, "예약되어있는 테마는 삭제할 수 없습니다."),

    WAITING_NOT_FOUND_BY_ID(HttpStatus.BAD_REQUEST, "예약 대기 정보가 존재하지 않습니다."),
    WAITING_NOT_FOUND_BY_RESERVATION(HttpStatus.BAD_REQUEST, "예약 정보와 일치하는 대기 정보가 존재하지 않습니다."),
    WAITING_NOT_REGISTER_BY_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 사용자에게 등록되거나 대기중인 예약이 있습니다."),

    PAYMENT_NOT_FOUND_BY_RESERVATION_ID(HttpStatus.BAD_REQUEST, "해당 예약 정보로 등록된 결제 정보가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
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
