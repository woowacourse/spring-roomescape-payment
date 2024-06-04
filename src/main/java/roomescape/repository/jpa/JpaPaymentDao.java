package roomescape.repository.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.Reservation;
import roomescape.domain.payment.Payment;

public interface JpaPaymentDao extends JpaRepository<Payment, Long> {
    @EntityGraph(attributePaths = {"reservation"})
    List<Payment> findAllByReservationIn(List<Reservation> reservations);
}
