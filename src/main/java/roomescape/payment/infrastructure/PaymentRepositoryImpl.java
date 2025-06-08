package roomescape.payment.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import roomescape.exception.payment.PaymentException;
import roomescape.payment.domain.PaymentInfo;
import roomescape.payment.domain.PaymentRepository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final JpaPaymentRepository jpaPaymentRepository;

    @Override
    public PaymentInfo save(PaymentInfo paymentInfo) {
        return jpaPaymentRepository.save(paymentInfo);
    }
}
