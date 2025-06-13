package roomescape.payment.repository.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.PaymentRepository;

@Repository
public interface PaymentRepositoryImpl extends PaymentRepository, JpaRepository<Payment, Long> {

}
