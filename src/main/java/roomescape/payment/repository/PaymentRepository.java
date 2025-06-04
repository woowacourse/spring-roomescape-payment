package roomescape.payment.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.payment.entity.Payment;

@RequiredArgsConstructor
@Repository
public class PaymentRepository implements PaymentRepositoryInterface {

    private final JpaPaymentRepository jpaPaymentRepository;

    @Override
    public Payment save(final Payment payment) {
        return jpaPaymentRepository.save(payment);
    }

    @Override
    public List<Payment> findAll() {
        return jpaPaymentRepository.findAll();
    }

    @Override
    public void deleteByReservationId(final Long reservationId) {
        jpaPaymentRepository.deleteByReservationId(reservationId);
    }
}
