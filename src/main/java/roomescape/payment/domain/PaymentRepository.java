package roomescape.payment.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findAllByPaymentProductIsIn(List<PaymentProduct> products);
}
