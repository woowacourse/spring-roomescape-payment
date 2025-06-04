package roomescape.payment.infrastructure;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.payment.domain.Payment;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
    @Query("""
                SELECT p FROM Payment p
                JOIN FETCH p.reservation r
                JOIN FETCH r.member m
                WHERE m.id = :memberId
            """)
    List<Payment> findAllByMemberId(@Param("memberId") Long memberId);
}
