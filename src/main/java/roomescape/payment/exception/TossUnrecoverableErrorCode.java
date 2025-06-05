package roomescape.payment.exception;

public enum TossUnrecoverableErrorCode {

    INVALID_API_KEY(true, "잘못된 시크릿키"),
    UNAUTHORIZED_KEY(true, "인증되지 않은 키"),
    FORBIDDEN_REQUEST(true, "허용되지 않은 요청"),
    INCORRECT_BASIC_AUTH_FORMAT(true, "잘못된 인증 형식"),
    INVALID_AUTHORIZE_AUTH(true, "유효하지 않은 인증 방식"),

    INVALID_UNREGISTERED_SUBMALL(true, "등록되지 않은 서브몰"),
    NOT_REGISTERED_BUSINESS(true, "등록되지 않은 사업자번호"),
    NOT_FOUND_TERMINAL_ID(true, "터미널 ID 없음"),

    ALREADY_PROCESSED_PAYMENT(true, "이미 처리된 결제"),
    NOT_FOUND_PAYMENT(true, "존재하지 않는 결제"),
    NOT_FOUND_PAYMENT_SESSION(true, "결제 세션 만료"),
    UNAPPROVED_ORDER_ID(true, "승인되지 않은 주문번호"),

    INVALID_REQUEST(true, "잘못된 요청"),

    FDS_ERROR(true, "위험거래 감지"),

    UNKNOWN_ERROR(false, "알 수 없는 에러");

    private final boolean unrecoverable;
    private final String description;

    TossUnrecoverableErrorCode(boolean unrecoverable, String description) {
        this.unrecoverable = unrecoverable;
        this.description = description;
    }

    public boolean isUnrecoverable() {
        return unrecoverable;
    }

    public String getDescription() {
        return description;
    }

    public static TossUnrecoverableErrorCode fromCode(String code) {
        try {
            return TossUnrecoverableErrorCode.valueOf(code);
        } catch (IllegalArgumentException e) {
            return UNKNOWN_ERROR;
        }
    }
}

