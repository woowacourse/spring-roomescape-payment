package roomescape.unit.fake;

import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.repository.PaymentRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FakePaymentRepository implements PaymentRepository {

    private final List<Payment> payments = new ArrayList<>();

    public FakePaymentRepository(Payment... payments) {
        this.payments.addAll(Arrays.asList(payments));
    }

    @Override
    public Payment save(Payment payment) {
        payments.add(payment);
        return payment;
    }

    @Override
    public Optional<Payment> findByReservation(Reservation reservation) {
        return payments.stream()
                .filter(payment -> payment.getReservation().equals(reservation))
                .findFirst();
    }
}
