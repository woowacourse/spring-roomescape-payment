package roomescape.domain.payment.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByMemberIdAndStatus(Long memberId, PaymentStatus status);

    Optional<Payment> findByReservationIdAndMemberIdAndStatus(Long reservationId, Long memberId, PaymentStatus status);

    Optional<Payment> findByReservationIdAndStatus(Long reservationId, PaymentStatus status);

    @Query("""
            select p.reservationId
            from Payment p
            where p.status = :status
            and p.reservationId in (:reservationIds)
            """)
    List<Long> findReservationIdsByStatusAndList(PaymentStatus status, List<Long> reservationIds);
}
