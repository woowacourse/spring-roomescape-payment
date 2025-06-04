package roomescape.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query(value = """
            select p
            from Payment p
            join fetch p.reservation
            where p.reservation.id = :reservationId
            """)
    Optional<Payment> findFetchByReservationId(@Param("reservationId") Long reservationId);
}
