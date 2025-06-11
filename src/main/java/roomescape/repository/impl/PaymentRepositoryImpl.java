package roomescape.repository.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.repository.jpa.PaymentJpaRepository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository repository;

    @Override
    public Payment save(Payment payment) {
        return repository.save(payment);
    }

    @Override
    public List<Payment> findByReservationIdIn(List<Long> reservationIds) {
        return repository.findByReservationIdIn(reservationIds);
    }
}
