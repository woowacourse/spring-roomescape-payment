package roomescape.domain.payment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CanceledPaymentRepository extends JpaRepository<CanceledPayment, Long> {
}
