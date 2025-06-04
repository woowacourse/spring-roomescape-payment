package roomescape.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import roomescape.reservation.domain.ReservationPayment;

@Repository
public interface ReservationPaymentRepository extends JpaRepository<ReservationPayment, Long> {
}
