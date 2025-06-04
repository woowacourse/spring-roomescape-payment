package roomescape.payment.domain.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.reservation.domain.Reservation;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByReservationId(Long reservationId);

    Payment findByReservationAndPaymentStatusIn(Reservation reservation, Collection<PaymentStatus> paymentStatuses);
}
