package roomescape.payment.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {


    @Query("""
            SELECT p
            FROM Payment p
            JOIN FETCH p.reservation r
            WHERE r.id = :id
                AND p.paymentStatus = :paymentStatus
            """)
    Payment findByReservationIdAndPaymentStatus(Long id, PaymentStatus paymentStatus);

    List<Payment> findByReservationId(Long id);
}
