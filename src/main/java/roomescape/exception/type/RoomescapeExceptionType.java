package roomescape.exception.type;

public enum RoomescapeExceptionType {
    EMPTY_NAME("이름은 필수 값입니다.",
            "Name is missing in the request."),
    EMPTY_TIME("시작 시간은 필수 값입니다.",
            "StartAt is missing in the request."),
    EMPTY_DATE("날짜는 필수값 입니다.",
            "Date is missing in the request."),
    EMPTY_THEME("테마는 필수값 입니다.",
            "Theme is missing in the request."),
    EMPTY_DESCRIPTION("테마 설명은 필수값 입니다.",
            "Theme description is missing in the request."),
    EMPTY_THUMBNAIL("테마 썸네일은 필수값 입니다.",
            "Theme thumbnail is missing in the request."),
    EMPTY_MEMBER("사용자 정보는 필수값 입니다.",
            "Member information is missing in the request."),

    PAST_TIME_RESERVATION("이미 지난 시간에 예약할 수 없습니다.",
            "Attempted to reserve a past time -> pastTime = %s"),

    DUPLICATE_RESERVATION("같은 시간에 이미 예약이 존재합니다.",
            "Duplicate reservation found for the same time. -> date = %s, themeId = %s, timeId = %s"),
    DUPLICATE_WAITING_RESERVATION("같은 시간에 이미 예약 혹은 예약 대기가 존재합니다.",
            "Duplicate reservation or waiting list found for the same time. -> memberId = %s, date = %s, themeId = %s, timeId = %s"),
    DUPLICATE_RESERVATION_TIME("이미 예약시간이 존재합니다.",
            "Reservation time already exists. -> startAt = %s"),
    DUPLICATE_THEME("이미 동일한 테마가 존재합니다.",
            "Duplicate theme found. -> themeName = %s"),

    DELETE_USED_TIME("예약이 존재하는 시간은 삭제할 수 없습니다.",
            "Cannot delete time with existing reservations. -> timeId = %s"),
    DELETE_USED_THEME("예약이 존재하는 테마는 삭제할 수 없습니다.",
            "Cannot delete theme with existing reservations. -> themeId = %s"),

    NOT_FOUND_RESERVATION_TIME("존재하지 않는 시간입니다.",
            "Reservation time not found. -> timeId = %s"),
    NOT_FOUND_RESERVATION_PAYMENT("결제가 존재하지 않는 예약입니다.",
            "Reservation payment not found. -> reservationId = %s"),
    NOT_FOUND_THEME("존재하지 않는 테마입니다.",
            "Theme not found. -> themeId = %s"),
    NOT_FOUND_MEMBER_BY_ID("존재하지 않는 사용자입니다.",
            "Member not found. -> memberId = %s"),
    NOT_FOUND_MEMBER_BY_EMAIL("존재하지 않는 이메일입니다.",
            "Member not found. -> memberEmail = %s"),
    NOT_FOUND_ROLE("존재하지 않는 권한 정보입니다.",
            "Role not found."),

    WRONG_PASSWORD("잘못된 비밀번호입니다.",
            "Incorrect password provided. -> password = %s"),
    REQUIRED_LOGIN("로그인이 필요합니다.",
            "Login is required."),
    PERMISSION_DENIED("접근 권한이 없습니다.",
            "Access denied due to insufficient permissions. -> role = %s"),

    INVALID_DATE_TIME_FORMAT("해석할 수 없는 날짜, 시간 포맷입니다.",
            "Invalid date or time format."),
    INVALID_PARSE_FORMAT("적합하지 않은 포맷으로 파싱에 실패하였습니다.",
            "Invalid json parsing format."),
    NO_QUERY_PARAMETER("필수 검색 조건이 누락되었습니다. 요청을 다시 확인해 주세요",
            "Missing required query parameter."),
    UN_EXPECTED_ERROR("예상치 못한 오류입니다. 서버 관계자에게 문의하세요.",
            "Unexpected error occurred.");

    private final String message;
    private final String logMessageFormat;

    RoomescapeExceptionType(String message, String logMessageFormat) {
        this.message = message;
        this.logMessageFormat = logMessageFormat;
    }

    public String getMessage() {
        return message;
    }

    public String getLogMessageFormat() {
        return logMessageFormat;
    }
}
