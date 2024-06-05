package roomescape.domain.payment;

enum PaymentApiErrorCode {
    UNKNOWN,
    ALREADY_PROCESSED_PAYMENT,
    PROVIDER_ERROR,
    EXCEED_MAX_CARD_INSTALLMENT_PLAN,
    INVALID_REQUEST,
    NOT_ALLOWED_POINT_USE,
    INVALID_API_KEY,
    INVALID_REJECT_CARD,
    BELOW_MINIMUM_AMOUNT,
    INVALID_CARD_EXPIRATION,
    INVALID_STOPPED_CARD,
    EXCEED_MAX_DAILY_PAYMENT_COUNT,
    NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT,
    INVALID_CARD_INSTALLMENT_PLAN,
    NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN,
    EXCEED_MAX_PAYMENT_AMOUNT,
    NOT_FOUND_TERMINAL_ID,
    INVALID_AUTHORIZE_AUTH,
    INVALID_CARD_LOST_OR_STOLEN,
    RESTRICTED_TRANSFER_ACCOUNT,
    INVALID_CARD_NUMBER,
    INVALID_UNREGISTERED_SUBMALL,
    NOT_REGISTERED_BUSINESS,
    EXCEED_MAX_ONE_DAY_WITHDRAW_AMOUNT,
    EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT,
    CARD_PROCESSING_ERROR,
    EXCEED_MAX_AMOUNT,
    INVALID_ACCOUNT_INFO_RE_REGISTER,
    NOT_AVAILABLE_PAYMENT,
    UNAPPROVED_ORDER_ID,
    UNAUTHORIZED_KEY,
    REJECT_ACCOUNT_PAYMENT,
    REJECT_CARD_PAYMENT,
    REJECT_CARD_COMPANY,
    FORBIDDEN_REQUEST,
    REJECT_TOSSPAY_INVALID_ACCOUNT,
    EXCEED_MAX_AUTH_COUNT,
    EXCEED_MAX_ONE_DAY_AMOUNT,
    NOT_AVAILABLE_BANK,
    INVALID_PASSWORD,
    INCORRECT_BASIC_AUTH_FORMAT,
    FDS_ERROR,
    NOT_FOUND_PAYMENT,
    NOT_FOUND_PAYMENT_SESSION,
    FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING,
    FAILED_INTERNAL_SYSTEM_PROCESSING,
    UNKNOWN_PAYMENT_ERROR
}