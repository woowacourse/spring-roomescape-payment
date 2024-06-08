package roomescape.payment.domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PaymentMatcher {

    private final Map<Long, Payment> paymentMatcher;

    public PaymentMatcher(List<Payment> payments) {
        this.paymentMatcher = convertPayments(payments);
    }

    private Map<Long, Payment> convertPayments(List<Payment> payments) {
        return payments.stream()
                .collect(Collectors.toMap(Payment::getReservationId, payment -> payment));
    }

    public Payment getPaymentByReservationId(Long reservationId) {
        Payment payment = paymentMatcher.get(reservationId);
        if (payment == null) {
            return new Payment(null, null, null, null, null);
        }
        return payment;
    }
}
