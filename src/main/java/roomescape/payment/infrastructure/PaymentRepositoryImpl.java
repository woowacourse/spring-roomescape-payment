package roomescape.payment.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.exception.resource.ResourceNotFoundException;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentRepository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final JpaPaymentRepository jpaPaymentRepository;

    @Override
    public Payment save(final Payment payment) {
        return jpaPaymentRepository.save(payment);
    }

    @Override
    public Payment getById(final Long paymentId) {
        return jpaPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 결제 정보를 찾을 수 없습니다. id = " + paymentId));
    }
}
