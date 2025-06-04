package roomescape.infrastructure.payment;

import java.util.List;
import org.springframework.stereotype.Repository;
import roomescape.business.model.entity.Payment;
import roomescape.business.model.repository.PaymentRepository;

@Repository
public class JpaPaymentRepository implements PaymentRepository {
    private final JpaPaymentDao dao;

    public JpaPaymentRepository(JpaPaymentDao dao) {
        this.dao = dao;
    }

    public List<Payment> findAll() {
        return dao.findAll();
    }

    @Override
    public void save(Payment payment) {
        dao.save(payment);
    }
}
