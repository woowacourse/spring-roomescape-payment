package roomescape.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
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

    @Override
    public List<Payment> findAllByReservationIn(List<Reservation> reservations) {
        return jpaPaymentDao.findAllByReservationIn(reservations);
    }

    @Override
    public Optional<Payment> findByOrderIdAndPaymentKey(String orderId, String paymentKey) {
        return jpaPaymentDao.findByOrderIdAndPaymentKey(orderId, paymentKey);
    }

    @Override
    public Optional<Payment> findByReservationId(Long reservationId) {
        return jpaPaymentDao.findByReservationId(reservationId);
    }

    @Override
    public void deleteByReservationId(Long reservationId) {
        jpaPaymentDao.deleteByReservationId(reservationId);
    }
}
