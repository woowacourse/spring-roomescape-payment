package roomescape.reservation.controller.response;

import roomescape.payment.toss.domain.TossPayment;

public record MyPaymentInfo(String paymentKey, Long amount) {
    public static MyPaymentInfo from(TossPayment tossPayment) {
        if (tossPayment == null) {
            return null;
        }
        return new MyPaymentInfo(tossPayment.getPaymentKey(), tossPayment.getAmount());
    }
}
