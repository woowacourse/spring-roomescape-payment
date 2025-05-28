package roomescape.payment.repository;

import lombok.RequiredArgsConstructor;
import roomescape.payment.domain.Payment;

@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }
}
