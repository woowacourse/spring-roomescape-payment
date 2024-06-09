package roomescape.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.core.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByReservationId(final Long reservationId);

    Boolean existsByReservationId(final Long reservationId);

    void deleteByReservationId(final Long reservationId);
}
