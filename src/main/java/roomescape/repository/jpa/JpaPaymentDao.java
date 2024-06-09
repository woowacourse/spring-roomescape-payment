package roomescape.repository.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.Reservation;
import roomescape.domain.payment.Payment;

public interface JpaPaymentDao extends JpaRepository<Payment, Long> {

    public List<Payment> findAllByReservationIn(List<Reservation> reservations);

    public Optional<Payment> findByOrderIdAndPaymentKey(String orderId, String paymentKey);

    public Optional<Payment> findByReservationId(Long reservationId);

    public void deleteByReservationId(Long reservationId);
}
