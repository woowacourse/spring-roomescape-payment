package roomescape.reservation.payment.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import roomescape.reservation.domain.ReservationId;
import roomescape.reservation.payment.domain.Payment;
import roomescape.reservation.payment.domain.PaymentId;

public interface PaymentRepository extends CrudRepository<Payment, PaymentId> {

    List<Payment> findAll();

    void deleteByReservationId(ReservationId reservationId);
}
