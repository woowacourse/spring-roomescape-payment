package roomescape.payment.infrastructure;

import org.springframework.data.repository.CrudRepository;
import roomescape.payment.domain.Payment;

import java.util.Optional;

public interface JpaPaymentRepository extends CrudRepository<Payment, Long> {

    Optional<Payment> findByReservationId(Long reservationId);

    void deleteByReservationId(Long reservationId);
}
