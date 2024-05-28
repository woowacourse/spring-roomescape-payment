package roomescape.infrastructure.payment;

import org.springframework.stereotype.Repository;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    public PaymentRepositoryImpl(PaymentJpaRepository paymentJpaRepository) {
        this.paymentJpaRepository = paymentJpaRepository;
    }

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }
}
