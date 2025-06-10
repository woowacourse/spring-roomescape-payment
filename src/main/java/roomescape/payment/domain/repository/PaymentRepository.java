package roomescape.payment.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByReservationId(Long reservationId);

    @Query("""
            SELECT p FROM Payment p
            JOIN FETCH p.reservation r
            JOIN FETCH r.theme
            JOIN FETCH r.time
            WHERE r.member.id = :memberId
            AND p.paymentStatus IN :statuses
            """)
    List<Payment> findByMemberIdAndStatusWithAssociations(
            @Param("memberId") Long memberId,
            @Param("statuses") List<PaymentStatus> statuses
    );
}
