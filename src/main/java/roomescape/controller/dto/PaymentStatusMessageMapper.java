package roomescape.controller.dto;

import roomescape.domain.reservation.Payment;

public class PaymentStatusMessageMapper {

    protected PaymentStatusMessageMapper() {
    }

    public static String mapToPaymentKey(Payment payment) {
        if (payment == null) {
            return "결제 대기중";
        }
        return payment.getPaymentKey();
    }

    public static String mapToAmount(Payment payment) {
        if (payment == null) {
            return "결제 대기중";
        }
        return payment.getAmount().toString();
    }
}
