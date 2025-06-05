package roomescape.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.repository.jpa.PaymentJpaRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository repository;

    @Override
    public void save(Payment payment) {
        repository.save(payment);
    }

    @Override
    public Optional<Payment> findByReservationId(final Long reservationId) {
        return repository.findByReservation_Id(reservationId);
    }

    @Override
    public void deleteByReservationId(Long reservationId) {
        repository.deleteByReservation_Id(reservationId);
    }
}
