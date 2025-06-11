package roomescape.repository.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.payment.Payment;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByReservationIdIn(List<Long> reservationIds);
}
