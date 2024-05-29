package roomescape.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import roomescape.domain.payment.Payment;

public class CollectionPaymentRepository implements PaymentRepository {

    private final List<Payment> payments;
    private final AtomicLong atomicLong;

    public CollectionPaymentRepository() {
        this.payments = new ArrayList<>();
        this.atomicLong = new AtomicLong(0);
    }

    @Override
    public Payment save(Payment payment) {
        Payment saved = new Payment(atomicLong.incrementAndGet(), payment);
        payments.add(saved);
        return saved;
    }
}
