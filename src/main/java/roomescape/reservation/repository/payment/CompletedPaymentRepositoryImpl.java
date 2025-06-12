package roomescape.reservation.repository.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.reservation.domain.CompletedPayment;

@Repository
@RequiredArgsConstructor
public class CompletedPaymentRepositoryImpl implements CompletedPaymentRepository {

    private final CompletedPaymentJpaRepository completedPaymentJpaRepository;

    @Override
    public CompletedPayment save(CompletedPayment completedPayment) {
        return completedPaymentJpaRepository.save(completedPayment);
    }

    @Override
    public boolean existsByReservationId(Long reservationId) {
        return completedPaymentJpaRepository.existsByReservationId(reservationId);
    }
}
