package roomescape.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import roomescape.domain.Payment;

@Repository
public interface JpaPaymentRepository extends JpaRepository<Payment, Long> {

}
