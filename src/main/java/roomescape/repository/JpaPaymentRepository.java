package roomescape.repository;

import org.springframework.stereotype.Repository;
import roomescape.domain.payment.Payment;
import roomescape.repository.jpa.JpaPaymentDao;

@Repository
public class JpaPaymentRepository implements PaymentRepository {
    private final JpaPaymentDao jpaPaymentDao;

    public JpaPaymentRepository(JpaPaymentDao jpaPaymentDao) {
        this.jpaPaymentDao = jpaPaymentDao;
    }

    @Override
    public Payment save(Payment payment) {
        return jpaPaymentDao.save(payment);
    }
}
