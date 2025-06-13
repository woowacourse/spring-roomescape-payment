package roomescape.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.entity.Payment;
import roomescape.entity.Reservation;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
            SELECT p FROM Payment p WHERE p.reservation IN :reservations
            """)
    List<Payment> findAllByReservations(@Param("reservations") List<Reservation> reservations);
}
