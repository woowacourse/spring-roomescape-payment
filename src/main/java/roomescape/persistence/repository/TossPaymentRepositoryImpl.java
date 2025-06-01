package roomescape.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.infrastructure.db.TossPaymentJpaRepository;
import roomescape.model.TossPayment;

@Repository
@RequiredArgsConstructor
public class TossPaymentRepositoryImpl implements TossPaymentRepository {

    private final TossPaymentJpaRepository jpaRepository;

    @Override
    public TossPayment save(TossPayment tossPayment) {
        return jpaRepository.save(tossPayment);
    }
}
