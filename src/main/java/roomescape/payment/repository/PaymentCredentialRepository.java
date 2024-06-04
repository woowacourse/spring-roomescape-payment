package roomescape.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.model.PaymentCredential;

public interface PaymentCredentialRepository extends JpaRepository<PaymentCredential, Long> {

    boolean existsByOrderIdAndAmount(String orderId, Long amount);

    void deleteAllByOrderIdAndAmount(String orderId, Long amount);
}
