package roomescape.infrastructure.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import roomescape.domain.payment.Payment;

@Repository
public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    Payment save(Payment payment);
}
