package roomescape.global.exception;

import java.util.Arrays;

public class PaymentFailException extends RuntimeException {

    private final String code;

    public PaymentFailException(String code, String message) {
        super(message);
        this.code = code;
    }

    public PaymentFailException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public PaymentFailException(String message, Throwable cause) {
        super(message, cause);
        this.code = null;
    }

    public boolean isClientError() {
        return TossPaymentConfirmClientErrorCode.isClientError(code);
    }

    enum TossPaymentConfirmClientErrorCode {
        ALREADY_PROCESSED_PAYMENT,
        EXCEED_MAX_CARD_INSTALLMENT_PLAN,
        NOT_ALLOWED_POINT_USE,
        BELOW_MINIMUM_AMOUNT,
        INVALID_CARD_EXPIRATION,
        INVALID_STOPPED_CARD,
        EXCEED_MAX_DAILY_PAYMENT_COUNT,
        NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT,
        INVALID_CARD_INSTALLMENT_PLAN,
        NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN,
        EXCEED_MAX_PAYMENT_AMOUNT,
        INVALID_CARD_LOST_OR_STOLEN,
        INVALID_CARD_NUMBER,
        EXCEED_MAX_ONE_DAY_WITHDRAW_AMOUNT,
        EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT,
        EXCEED_MAX_AMOUNT,
        INVALID_ACCOUNT_INFO_RE_REGISTER,
        UNAPPROVED_ORDER_ID,
        REJECT_ACCOUNT_PAYMENT,
        REJECT_CARD_PAYMENT,
        REJECT_TOSSPAY_INVALID_ACCOUNT,
        EXCEED_MAX_ONE_DAY_AMOUNT,
        NOT_AVAILABLE_BANK,
        INVALID_PASSWORD,
        FDS_ERROR,
        NOT_FOUND_PAYMENT_SESSION,
        ;

        static boolean isClientError(String code) {
            return Arrays.stream(values())
                    .anyMatch(errorCode -> errorCode.name().equals(code));
        }
    }
}
