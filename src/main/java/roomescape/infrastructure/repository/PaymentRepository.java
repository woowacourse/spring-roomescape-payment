package roomescape.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.domain.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
        SELECT p
        FROM Payment p
        JOIN p.reservation r
        WHERE r.member.id = :memberId
    """)
    List<Payment> findAllByMemberId(Long memberId);
}
