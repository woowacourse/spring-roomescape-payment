package roomescape.reservation.domain;

import roomescape.payment.domain.Payment;

public record ReservationPayment(Reservation reservation, Payment payment) {

    public String paymentKey() {
        if (payment == null) {
            return null;
        }
        return payment.getPaymentKey();
    }

    public Long totalAmount() {
        if (payment == null) {
            return null;
        }
        return payment.getTotalAmount();
    }
}
