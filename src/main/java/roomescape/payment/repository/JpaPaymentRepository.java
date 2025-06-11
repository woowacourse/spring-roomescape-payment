package roomescape.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.payment.domain.Payment;

import java.util.List;

public interface JpaPaymentRepository extends JpaRepository<Payment, Long>, PaymentRepository {

    @Query("""
            SELECT p FROM Payment p
            WHERE p.reservation.id IN :reservationIds
            """)
    List<Payment> findAllByReservationIds(@Param("reservationIds") List<Long> reservationIds);
}
