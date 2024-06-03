package roomescape.service.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class TossPaymentErrorCodeUtils {

    private final Set<String> displayableErrorCodes;

    public TossPaymentErrorCodeUtils() {
        this.displayableErrorCodes = new HashSet<>(Arrays.asList(
            "ALREADY_PROCESSED_PAYMENT",
            "EXCEED_MAX_CARD_INSTALLMENT_PLAN",
            "NOT_ALLOWED_POINT_USE",
            "INVALID_REJECT_CARD",
            "BELOW_MINIMUM_AMOUNT",
            "INVALID_CARD_EXPIRATION",
            "INVALID_STOPPED_CARD",
            "EXCEED_MAX_DAILY_PAYMENT_COUNT",
            "NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT",
            "INVALID_CARD_INSTALLMENT_PLAN",
            "NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN",
            "EXCEED_MAX_PAYMENT_AMOUNT",
            "INVALID_CARD_LOST_OR_STOLEN",
            "RESTRICTED_TRANSFER_ACCOUNT",
            "INVALID_CARD_NUMBER",
            "EXCEED_MAX_ONE_DAY_WITHDRAW_AMOUNT",
            "EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT",
            "EXCEED_MAX_AMOUNT",
            "INVALID_ACCOUNT_INFO_RE_REGISTER",
            "NOT_AVAILABLE_PAYMENT",
            "REJECT_ACCOUNT_PAYMENT",
            "REJECT_CARD_PAYMENT",
            "REJECT_TOSSPAY_INVALID_ACCOUNT",
            "EXCEED_MAX_AUTH_COUNT",
            "EXCEED_MAX_ONE_DAY_AMOUNT",
            "NOT_AVAILABLE_BANK",
            "INVALID_PASSWORD",
            "FDS_ERROR",
            "UNKNOWN_PAYMENT_ERROR"
        ));
    }

    public boolean isNotDisplayableErrorCode(String errorCode) {
        return !displayableErrorCodes.contains(errorCode);
    }
}
