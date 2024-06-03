package roomescape.payment.infrastructure;

import java.util.Arrays;
import java.util.Set;
import org.springframework.http.HttpStatus;

public enum PaymentApprovalErrorCode {

    PAYMENT_APPROVAL_ERROR_INVALID_USER_INPUT(
            Set.of(
                    TossErrorCode.ALREADY_PROCESSED_PAYMENT,
                    TossErrorCode.PROVIDER_ERROR,
                    TossErrorCode.EXCEED_MAX_CARD_INSTALLMENT_PLAN,
                    TossErrorCode.INVALID_REQUEST,
                    TossErrorCode.NOT_ALLOWED_POINT_USE,
                    TossErrorCode.INVALID_API_KEY,
                    TossErrorCode.INVALID_REJECT_CARD,
                    TossErrorCode.BELOW_MINIMUM_AMOUNT,
                    TossErrorCode.INVALID_CARD_EXPIRATION,
                    TossErrorCode.INVALID_STOPPED_CARD,
                    TossErrorCode.EXCEED_MAX_DAILY_PAYMENT_COUNT,
                    TossErrorCode.NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT,
                    TossErrorCode.INVALID_CARD_INSTALLMENT_PLAN,
                    TossErrorCode.NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN,
                    TossErrorCode.EXCEED_MAX_PAYMENT_AMOUNT,
                    TossErrorCode.NOT_FOUND_TERMINAL_ID,
                    TossErrorCode.INVALID_AUTHORIZE_AUTH,
                    TossErrorCode.INVALID_CARD_LOST_OR_STOLEN,
                    TossErrorCode.RESTRICTED_TRANSFER_ACCOUNT,
                    TossErrorCode.INVALID_CARD_NUMBER,
                    TossErrorCode.INVALID_UNREGISTERED_SUBMALL,
                    TossErrorCode.NOT_REGISTERED_BUSINESS,
                    TossErrorCode.EXCEED_MAX_ONE_DAY_WITHDRAW_AMOUNT,
                    TossErrorCode.EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT,
                    TossErrorCode.CARD_PROCESSING_ERROR,
                    TossErrorCode.EXCEED_MAX_AMOUNT,
                    TossErrorCode.INVALID_ACCOUNT_INFO_RE_REGISTER,
                    TossErrorCode.NOT_AVAILABLE_PAYMENT,
                    TossErrorCode.UNAPPROVED_ORDER_ID,
                    TossErrorCode.NOT_FOUND_PAYMENT,
                    TossErrorCode.NOT_FOUND_PAYMENT_SESSION
            ),
            HttpStatus.BAD_REQUEST
    ),
    PAYMENT_APPROVAL_ERROR_FORBIDDEN(
            Set.of(
                    TossErrorCode.REJECT_ACCOUNT_PAYMENT,
                    TossErrorCode.REJECT_CARD_PAYMENT,
                    TossErrorCode.REJECT_CARD_COMPANY,
                    TossErrorCode.FORBIDDEN_REQUEST,
                    TossErrorCode.REJECT_TOSSPAY_INVALID_ACCOUNT,
                    TossErrorCode.EXCEED_MAX_AUTH_COUNT,
                    TossErrorCode.EXCEED_MAX_ONE_DAY_AMOUNT,
                    TossErrorCode.NOT_AVAILABLE_BANK,
                    TossErrorCode.INVALID_PASSWORD,
                    TossErrorCode.FDS_ERROR
            ),
            HttpStatus.FORBIDDEN
    ),
    PAYMENT_APPROVAL_ERROR_EXTERNAL_SERVER(
            Set.of(
                    TossErrorCode.FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING,
                    TossErrorCode.FAILED_INTERNAL_SYSTEM_PROCESSING,
                    TossErrorCode.UNKNOWN_PAYMENT_ERROR
            ),
            HttpStatus.SERVICE_UNAVAILABLE
    ),
    PAYMENT_APPROVAL_ERROR_UNIDENTIFIED_ERROR(
            Set.of(

            ),
            HttpStatus.SERVICE_UNAVAILABLE
    );

    private final Set<TossErrorCode> tossErrorCodes;
    private final HttpStatus statusCode;

    PaymentApprovalErrorCode(Set<TossErrorCode> tossErrorCodes, HttpStatus statusCode) {
        this.tossErrorCodes = tossErrorCodes;
        this.statusCode = statusCode;
    }

    public static PaymentApprovalErrorCode from(TossErrorCode tossErrorCode) {
        return Arrays.stream(values())
                .filter(errorCode -> errorCode.tossErrorCodes.contains(tossErrorCode))
                .findFirst()
                .orElse(PAYMENT_APPROVAL_ERROR_UNIDENTIFIED_ERROR);
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
