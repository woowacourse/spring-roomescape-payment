package roomescape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.model.Member;
import roomescape.model.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p INNER JOIN p.reservation r WHERE r.member = :member")
    List<Payment> findByMember(final Member member);
}
