package roomescape.controller.dto;

import roomescape.domain.reservation.Payment;

public class PaymentStatusMessageMapper {

    protected PaymentStatusMessageMapper() {
    }

    public static String mapToPaymentKey(Payment payment) {
        if (payment == null) {
            return "정보 없음";
        }
        return payment.getPaymentKey();
    }

    public static String mapToAmount(Payment payment) {
        if (payment == null) {
            return "정보 없음";
        }
        return payment.getAmount().toString();
    }
}
