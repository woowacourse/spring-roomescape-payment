package roomescape.exception;

public enum ErrorType {
    PAYMENT_SERVER_FAILED("Payment-001", "결제 진행중 서버측 오류가 발생했습니다.",500),
    SECURITY_EXCEPTION("SECURITY-001", "로그인이 필요합니다.",401),

    MEMBER_NOT_FOUND("AUTH-001", "해당 유저를 찾을 수 없습니다.",401),
    INVALID_TOKEN("AUTH-002", "유효하지 않은 토큰입니다.",401),
    TOKEN_PAYLOAD_EXTRACTION_FAILURE("AUTH-003", "토큰 페이로드 추출에 실패했습니다",401),

    MISSING_REQUIRED_VALUE_ERROR("COMMON-001", "필수 요청값이 누락되었습니다.",400),
    NOT_ALLOWED_PERMISSION_ERROR("COMMON-002", "허용되지 않은 권한입니다.",403),
    INVALID_REQUEST_ERROR("COMMON-003", "올바르지 않은 데이터 요청입니다.",400),

    NAME_FORMAT_ERROR("USER-001", "올바르지 않은 이름 입력 양식입니다.",400),
    EMAIL_FORMAT_ERROR("USER-002", "올바르지 않은 이메일 입력 양식입니다.",400),
    DUPLICATED_EMAIL_ERROR("USER-003", "중복된 이메일입니다.",400),

    DUPLICATED_RESERVATION_ERROR("RESERVATION-001", "중복된 예약입니다.",400),
    DUPLICATED_RESERVATION_TIME_ERROR("RESERVATION-002", "중복된 예약 시간입니다.",400),
    MEMBER_RESERVATION_NOT_FOUND("RESERVATION-003", "해당 ID에 대응되는 사용자 예약이 없습니다.",400),
    RESERVATION_NOT_DELETED("RESERVATION-004", "예약이 존재하여 삭제할 수 없습니다.",400),
    NOT_A_RESERVATION_MEMBER("RESERVATION-005", "예약자가 아닙니다.",400),
    NOT_A_WAITING_RESERVATION("RESERVATION-006", "대기 예약이 아닙니다.",400),

    THEME_NOT_FOUND("THEME-001", "해당 ID에 대응되는 테마가 없습니다.",404),

    RESERVATION_TIME_NOT_FOUND("RESERVATION-TIME-001", "해당 ID에 대응되는 예약 시간이 없습니다.",404),

    UNEXPECTED_SERVER_ERROR("SERVER-001", "서버 관리자에게 문의하세요.",500);

    private final String errorCode;
    private final String message;
    private final int statusCode;

    ErrorType(String errorCode, String message, final int statusCode) {
        this.errorCode = errorCode;
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
