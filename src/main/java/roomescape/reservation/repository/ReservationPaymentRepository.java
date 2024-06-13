package roomescape.reservation.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.reservation.domain.Payment;
import roomescape.reservation.domain.ReservationPayment;

public interface ReservationPaymentRepository extends JpaRepository<ReservationPayment, Long> {
    @Query("""
            SELECT p
            FROM ReservationPayment rp
            INNER JOIN Payment p ON p.id = rp.payment.id
            WHERE rp.reservation.id = :reservationId
            """)
    Optional<Payment> findPaymentByReservationId(Long reservationId);
}
