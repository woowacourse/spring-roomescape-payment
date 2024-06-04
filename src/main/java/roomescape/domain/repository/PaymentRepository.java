package roomescape.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import roomescape.domain.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
