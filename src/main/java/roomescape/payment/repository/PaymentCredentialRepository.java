package roomescape.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.model.PaymentCredential;

public interface PaymentCredentialRepository extends JpaRepository<PaymentCredential, Long> {
}
