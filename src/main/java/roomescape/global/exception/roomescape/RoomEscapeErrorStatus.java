package roomescape.global.exception.roomescape;

import org.springframework.http.HttpStatus;

public enum RoomEscapeErrorStatus {
    /**
     * Member Error Status
     */
    NON_EXIST_MEMBER(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    ALREADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),

    /**
     * Reservation Error Status
     */
    NON_EXIST_RESERVATION(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다."),
    ALREADY_EXIST_RESERVATION(HttpStatus.BAD_REQUEST, "이미 예약을 등록하였습니다."),
    RESERVED_TIME(HttpStatus.BAD_REQUEST, "이미 예약된 시간입니다."),
    WAITING_RESERVATION_REQUIRES_EXISTING(HttpStatus.BAD_REQUEST, "대기 예약은 기존 예약이 있을 때만 가능합니다."),
    ONLY_PENDING_RESERVATION_CAN_BE_DENIED(HttpStatus.BAD_REQUEST, "대기 상태의 예약만 거절할 수 있습니다."),

    /**
     * Reservation Theme Error Status
     */
    NON_EXIST_RESERVATION_THEME(HttpStatus.NOT_FOUND, "존재하지 않는 테마입니다."),
    ALREADY_EXIST_RESERVATION_THEME(HttpStatus.BAD_REQUEST, "이미 존재하는 테마입니다."),
    CANNOT_DELETE_THEME_WITH_RESERVATIONS(HttpStatus.BAD_REQUEST, "예약이 존재해 테마를 삭제할 수 없습니다."),

    /**
     * Reservation Time Error Status
     */
    NON_EXIST_RESERVATION_TIME(HttpStatus.NOT_FOUND, "존재하는 시간이 없습니다."),
    ALREADY_EXIST_RESERVATION_TIME(HttpStatus.BAD_REQUEST, "이미 존재하는 예약 시간 입니다."),
    CANNOT_DELETE_TIME_WITH_RESERVATIONS(HttpStatus.BAD_REQUEST, "이미 예약이 존재해 시간을 삭제할 수 없습니다."),

    /**
     * Reservation Item Error Status
     */
    NON_EXIST_RESERVATION_ITEM(HttpStatus.NOT_FOUND, "존재하지 않는 예약 항목입니다."),
    INVALID_RESERVATION_ITEM(HttpStatus.BAD_REQUEST, "예약시간은 과거일 수 없습니다."),
    ;

    RoomEscapeErrorStatus(HttpStatus httpStatus, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    public final HttpStatus httpStatus;
    public final String errorMessage;
}
