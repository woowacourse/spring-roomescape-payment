package roomescape.payment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import roomescape.payment.domain.Payment;

public interface PaymentRepository extends Repository<Payment, Long> {

    Payment save(Payment payment);

    @Query("""
            SELECT p from Payment p WHERE p.reservation.id = :reservationId
            """)
    Optional<Payment> findByReservationId(Long reservationId);

    void deleteByReservation_Id(Long reservationId);
}
