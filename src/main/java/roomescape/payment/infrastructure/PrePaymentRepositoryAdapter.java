package roomescape.payment.infrastructure;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.payment.domain.PrePayment;
import roomescape.payment.domain.PrePaymentRepository;

@Repository
@AllArgsConstructor
public class PrePaymentRepositoryAdapter implements PrePaymentRepository {
    private final PrePaymentJpaRepository prePaymentJpaRepository;

    @Override
    public Optional<PrePayment> findByOrderId(String orderId) {
        return prePaymentJpaRepository.findByOrderId(orderId);
    }

    @Override
    public void save(PrePayment prePayment) {
        prePaymentJpaRepository.save(prePayment);
    }
}
