package roomescape.payment.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
            SELECT p
            FROM Payment AS p
            WHERE p.reservationId = :reservationId
            """)
    Optional<Payment> findByReservationId(Long reservationId);

    @Query("""
            SELECT p
            FROM Payment AS p
            WHERE p.memberId = :memberId
            """)
    List<Payment> findAllByMemberId(Long memberId);
}
