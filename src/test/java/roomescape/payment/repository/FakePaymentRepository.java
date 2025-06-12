package roomescape.payment.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import roomescape.payment.domain.Payment;

public class FakePaymentRepository implements PaymentRepository {

    private final List<Payment> payments = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong index = new AtomicLong(1L);

    @Override
    public Payment save(final Payment payment) {
        Payment saved = Payment.withId(
                index.getAndIncrement(),
                payment.getPaymentKey(),
                payment.getOrderId(),
                payment.getAmount(),
                null,
                null
        );

        payments.add(saved);

        return saved;
    }

    @Override
    public Boolean existsByPaymentKey(final String paymentKey) {
        return payments.stream()
                .anyMatch(payment -> payment.getPaymentKey().equals(paymentKey));
    }

    @Override
    public Optional<Payment> findById(final Long id) {
        return payments.stream()
                .filter(payment -> payment.getId().equals(id))
                .findFirst();
    }
}
