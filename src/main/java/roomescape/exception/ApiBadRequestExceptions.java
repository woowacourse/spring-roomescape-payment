package roomescape.exception;

import java.util.stream.Stream;

public enum ApiBadRequestExceptions {
    INVALID_REQUEST,
    NOT_ALLOWED_POINT_USE,
    INVALID_REJECT_CARD,
    BELOW_MINIMUM_AMOUNT,
    INVALID_CARD_EXPIRATION,
    INVALID_STOPPED_CARD,
    EXCEED_MAX_DAILY_PAYMENT_COUNT,
    NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT,
    EXCEED_MAX_PAYMENT_AMOUNT,
    INVALID_CARD_LOST_OR_STOLEN,
    RESTRICTED_TRANSFER_ACCOUNT,
    INVALID_CARD_NUMBER,
    EXCEED_MAX_ONE_DAY_WITHDRAW_AMOUNT,
    EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT,
    INVALID_ACCOUNT_INFO_RE_REGISTER,
    NOT_AVAILABLE_PAYMENT,
    REJECT_ACCOUNT_PAYMENT,
    REJECT_TOSSPAY_INVALID_ACCOUNT,
    EXCEED_MAX_ONE_DAY_AMOUNT,
    NOT_AVAILABLE_BANK,
    INVALID_PASSWORD,
    FDS_ERROR,
    ;

    public static boolean isBadRequest(String target) {
        return Stream.of(values())
                .map(Enum::name)
                .anyMatch(errorName -> errorName.equals(target));
    }
}
