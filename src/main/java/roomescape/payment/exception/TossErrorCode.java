package roomescape.payment.exception;

import java.util.Arrays;

/**
 * 유저에게 에러 정보를 전달하면 안 되는 ErrorCode 모음입니다.
 */
public enum TossErrorCode {

    INVALID_API_KEY,
    INVALID_REQUEST,
    NOT_FOUND_TERMINAL_ID,
    INVALID_AUTHORIZE_AUTH,
    INVALID_CARD_LOST_OR_STOLEN,
    UNAPPROVED_ORDER_ID,
    UNAUTHORIZED_KEY,
    REJECT_CARD_COMPANY,
    FORBIDDEN_REQUEST,
    INCORRECT_BASIC_AUTH_FORMAT,
    FDS_ERROR,
    FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING,
    FAILED_INTERNAL_SYSTEM_PROCESSING,
    UNKNOWN_PAYMENT_ERROR,
    FORBIDDEN_CONSECUTIVE_REQUEST,
    NOT_FOUND,
    NOT_FOUND_PAYMENT,
    NOT_MATCHES_REFUNDABLE_AMOUNT,
    PROVIDER_ERROR,
    NOT_CANCELABLE_AMOUNT,
    FAILED_METHOD_HANDLING_CANCEL,
    COMMON_ERROR,
    INVALID_ORDER_ID,
    DUPLICATED_ORDER_ID,
    INVALID_REQUIRED_PARAM;

    public static boolean canSendTossMessage(String code) {
        return Arrays.stream(TossErrorCode.values())
            .noneMatch(e -> e.name().equals(code));
    }
}
