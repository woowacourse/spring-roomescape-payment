package roomescape.unit.fake;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import roomescape.domain.Payment;
import roomescape.domain.repository.PaymentRepository;

public class FakePaymentRepository implements PaymentRepository {

    private final List<Payment> payments = new ArrayList<>();
    private final AtomicLong index = new AtomicLong(1);

    @Override
    public Payment save(final Payment payment) {
        Payment paymentWithId = new Payment(index.getAndIncrement(), payment.getReservation(), payment.getPaymentKey(),
                payment.getOrderId(), payment.getAmount());

        payments.add(paymentWithId);
        return paymentWithId;
    }

    @Override
    public Optional<Payment> findByReservationId(final Long reservationId) {
        return payments.stream()
                .filter(payment -> payment.getReservation().getId().equals(reservationId))
                .findFirst();
    }
}
