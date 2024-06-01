package roomescape.domain.payment;

import static roomescape.domain.payment.PaymentApiErrorCode.EXCEED_MAX_PAYMENT_AMOUNT;
import static roomescape.domain.payment.PaymentApiErrorCode.INVALID_PASSWORD;
import static roomescape.domain.payment.PaymentApiErrorCode.INVALID_STOPPED_CARD;
import static roomescape.domain.payment.PaymentApiErrorCode.REJECT_ACCOUNT_PAYMENT;
import static roomescape.domain.payment.PaymentApiErrorCode.REJECT_CARD_COMPANY;
import static roomescape.domain.payment.PaymentApiErrorCode.REJECT_CARD_PAYMENT;
import static roomescape.exception.ExceptionType.PAYMENT_FAIL_CAUSE_BALANCE;
import static roomescape.exception.ExceptionType.PAYMENT_FAIL_CAUSE_CARD_COMPANY;
import static roomescape.exception.ExceptionType.PAYMENT_FAIL_CAUSE_EXCEED_MAX_ONE_DAY_AMOUNT;
import static roomescape.exception.ExceptionType.PAYMENT_FAIL_CAUSE_HIDDEN;
import static roomescape.exception.ExceptionType.PAYMENT_FAIL_CAUSE_INVALID_PASSWORD;
import static roomescape.exception.ExceptionType.PAYMENT_FAIL_CAUSE_INVALID_STOPPED_CARD;

import roomescape.exception.ExceptionType;

record PaymentApiError(PaymentApiErrorCode code, String message) {
    ExceptionType mapToExceptionType() {
        if (EXCEED_MAX_PAYMENT_AMOUNT.equals(code)) {
            return PAYMENT_FAIL_CAUSE_EXCEED_MAX_ONE_DAY_AMOUNT;
        }
        if (REJECT_CARD_COMPANY.equals(code)) {
            return PAYMENT_FAIL_CAUSE_CARD_COMPANY;
        }
        if (INVALID_PASSWORD.equals(code)) {
            return PAYMENT_FAIL_CAUSE_INVALID_PASSWORD;
        }
        if (REJECT_ACCOUNT_PAYMENT.equals(code) || REJECT_CARD_PAYMENT.equals(code)) {
            return PAYMENT_FAIL_CAUSE_BALANCE;
        }
        if (INVALID_STOPPED_CARD.equals(code)) {
            return PAYMENT_FAIL_CAUSE_INVALID_STOPPED_CARD;
        }
        return PAYMENT_FAIL_CAUSE_HIDDEN;
    }
}
