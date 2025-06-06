package roomescape.mvc.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import roomescape.mvc.payment.domain.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
