package roomescape.infrastructure.repository;


import org.springframework.data.repository.Repository;
import roomescape.domain.payment.Payment;

public interface PaymentJpaRepository extends Repository<Payment, Long> {

    Payment save(Payment payment);
}
