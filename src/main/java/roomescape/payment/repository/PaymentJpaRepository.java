package roomescape.payment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import roomescape.payment.domain.Payment;

@Repository
public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByReservationIdIn(final List<Long> reservationIds);
}
