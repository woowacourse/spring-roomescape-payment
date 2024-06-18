package roomescape.system.exception;

public enum ErrorType {

    // 400 Bad Request
    REQUEST_DATA_BLANK("요청 데이터에 유효하지 않은 값(null OR 공백)이 포함되어있습니다."),
    INVALID_REQUEST_DATA_TYPE("요청 데이터 형식이 올바르지 않습니다."),
    INVALID_REQUEST_DATA("요청 데이터 값이 올바르지 않습니다."),
    INVALID_DATE_RANGE("종료 날짜는 시작 날짜 이전일 수 없습니다."),
    HAS_RESERVATION_OR_WAITING("같은 테마에 대한 예약(대기)는 한 번만 가능합니다."),

    // 401 Unauthorized
    EXPIRED_TOKEN("토큰이 만료되었습니다. 다시 로그인 해주세요."),
    UNSUPPORTED_TOKEN("지원하지 않는 JWT 토큰입니다."),
    MALFORMED_TOKEN("형식이 맞지 않는 JWT 토큰입니다."),
    INVALID_SIGNATURE_TOKEN("잘못된 JWT 토큰 Signature 입니다."),
    ILLEGAL_TOKEN("JWT 토큰의 Claim 이 비어있습니다."),
    INVALID_TOKEN("JWT 토큰이 존재하지 않거나 유효하지 않습니다."),
    NOT_EXIST_COOKIE("쿠키가 존재하지 않습니다. 로그인이 필요한 서비스입니다."),

    // 403 Forbidden
    LOGIN_REQUIRED("로그인이 필요한 서비스입니다."),
    PERMISSION_DOES_NOT_EXIST("접근 권한이 존재하지 않습니다."),

    // 404 Not Found
    MEMBER_NOT_FOUND("회원(Member) 정보가 존재하지 않습니다."),
    RESERVATION_NOT_FOUND("예약(Reservation) 정보가 존재하지 않습니다."),
    RESERVATION_TIME_NOT_FOUND("예약 시간(ReservationTime) 정보가 존재하지 않습니다."),
    THEME_NOT_FOUND("테마(Theme) 정보가 존재하지 않습니다."),
    PAYMENT_NOT_POUND("결제(Payment) 정보가 존재하지 않습니다."),

    // 405 Method Not Allowed
    METHOD_NOT_ALLOWED("지원하지 않는 HTTP Method 입니다."),

    // 409 Conflict
    TIME_IS_USED_CONFLICT("삭제할 수 없는 시간대입니다. 예약이 존재하는지 확인해주세요."),
    THEME_IS_USED_CONFLICT("삭제할 수 없는 테마입니다. 예약이 존재하는지 확인해주세요."),
    TIME_DUPLICATED("이미 해당 시간이 존재합니다."),
    THEME_DUPLICATED("같은 이름의 테마가 존재합니다."),
    RESERVATION_DUPLICATED("해당 시간에 이미 예약이 존재합니다."),
    RESERVATION_PERIOD_IN_PAST("이미 지난 시간대는 예약할 수 없습니다."),
    CANCELED_BEFORE_PAYMENT("취소 시간이 결제 시간 이전일 수 없습니다."),

    // 500 Internal Server Error,
    INTERNAL_SERVER_ERROR("서버 내부에서 에러가 발생하였습니다."),

    // Payment Error
    PAYMENT_ERROR("결제(취소)에 실패했습니다. 결제(취소) 정보를 확인해주세요."),
    PAYMENT_SERVER_ERROR("결제 서버에서 에러가 발생하였습니다. 잠시 후 다시 시도해주세요.");

    private final String description;

    ErrorType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
