package roomescape.repository;

import java.util.List;
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
    public void deleteByReservationId(long reservationId) {
        jpaPaymentDao.deleteByReservation_Id(reservationId);
    }
}
