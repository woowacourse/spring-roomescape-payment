package roomescape.payment.infrastructure.db.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.model.entity.Payment;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByReservationIdIn(List<Long> reservationIds);
}
