package roomescape.exception;

import java.util.Arrays;

public enum TossPaymentApiUserExceptionCode {
    NOT_ALLOWED_POINT_USE,
    INVALID_REJECT_CARD,
    INVALID_CARD_EXPIRATION,
    INVALID_STOPPED_CARD,
    EXCEED_MAX_DAILY_PAYMENT_COUNT,
    NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT,
    INVALID_CARD_LOST_OR_STOLEN,
    RESTRICTED_TRANSFER_ACCOUNT,
    EXCEED_MAX_AMOUNT,
    INVALID_ACCOUNT_INFO_RE_REGISTER,
    REJECT_ACCOUNT_PAYMENT,
    REJECT_CARD_PAYMENT,
    REJECT_TOSSPAY_INVALID_ACCOUNT,
    EXCEED_MAX_AUTH_COUNT,
    NOT_AVAILABLE_BANK,
    INVALID_PASSWORD,
    FDS_ERROR;

    public static boolean hasErrorCode(String code) {
        return Arrays.stream(values())
                .anyMatch(e -> e.name().equals(code));
    }
}
