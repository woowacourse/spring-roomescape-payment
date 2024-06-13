package roomescape.core.exception;

public enum ExceptionMessage {
    TOKEN_NOT_FOUND_EXCEPTION("토큰이 존재하지 않습니다."),
    INVALID_EMAIL_OR_PASSWORD_EXCEPTION("올바르지 않은 이메일 또는 비밀번호입니다."),

    ALREADY_USED_EMAIL_EXCEPTION("이미 사용 중인 이메일입니다."),
    MEMBER_NOT_FOUND_EXCEPTION("존재하지 않는 사용자입니다."),
    RESERVATION_NOT_FOUND_EXCEPTION("존재하지 않는 예약입니다."),
    PAYMENT_NOT_FOUND_EXCEPTION("해당 예약의 결제 내역이 존재하지 않습니다."),
    TIME_NOT_FOUND_EXCEPTION("존재하지 않는 예약 시간입니다."),
    THEME_NOT_FOUND_EXCEPTION("존재하지 않는 테마입니다."),
    ALREADY_BOOKED_TIME_EXCEPTION("해당 시간에 이미 예약 내역이 존재합니다."),

    DUPLICATED_TIME_EXCEPTION("해당 시간이 이미 존재합니다."),
    BOOKED_TIME_DELETE_EXCEPTION("예약 내역이 존재하는 시간은 삭제할 수 없습니다."),

    THEME_NAME_DUPLICATED_EXCEPTION("해당 이름의 테마가 이미 존재합니다."),
    BOOKED_THEME_DELETE_EXCEPTION("예약 내역이 존재하는 테마는 삭제할 수 없습니다."),

    BOOKED_TIME_WAITING_EXCEPTION("해당 시간에 이미 예약한 내역이 존재합니다. 예약 대기할 수 없습니다."),
    WAITED_TIME_WAITING_EXCEPTION("해당 시간에 이미 예약 대기한 내역이 존재합니다. 예약 대기할 수 없습니다."),
    ALLOWED_TO_ADMIN_ONLY_EXCEPTION("관리자만 삭제할 수 있습니다."),
    WAITING_IS_NOT_YOURS_EXCEPTION("본인의 예약 대기만 취소할 수 있습니다."),
    WAITING_NOT_FOUND_EXCEPTION("존재하지 않는 예약 대기입니다.");

    private final String value;

    ExceptionMessage(final String value) {
        this.value = value;
    }

    public String getMessage() {
        return value;
    }
}
