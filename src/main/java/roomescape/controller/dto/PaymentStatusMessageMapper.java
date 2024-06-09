package roomescape.controller.dto;

import static roomescape.domain.reservation.Payment.EMPTY_PAYMENT;

import roomescape.domain.reservation.Payment;

public class PaymentStatusMessageMapper {

    protected PaymentStatusMessageMapper() {
    }

    public static String mapToPaymentKey(Payment payment) {
        if (payment.equals(EMPTY_PAYMENT)) {
            return "예약 대기중";
        }
        return payment.getPaymentKey();
    }

    public static String mapToAmount(Payment payment) {
        if (payment.equals(EMPTY_PAYMENT)) {
            return "예약 대기중";
        }
        return payment.getAmount().toString();
    }
}
