package roomescape.infrastructure;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.exception.NotFoundException;

public interface PaymentJpaRepository extends PaymentRepository, Repository<Payment, Long> {

    @Override
    default Payment getById(final Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("존재하지 않는 결제 정보입니다. id : " + id));
    }

    @Modifying
    @Query("DELETE FROM PAYMENT p WHERE p.id = :id")
    @Transactional
    int deleteByIdAndCount(final Long id);

    @Transactional
    default void deleteByIdOrElseThrow(final Long id) {
        var deletedCount = deleteByIdAndCount(id);
        if (deletedCount == 0) {
            throw new NotFoundException("존재하지 않는 결제 정보입니다. id : " + id);
        }
    }
}
