package roomescape.repository;

import java.util.List;
import roomescape.domain.Reservation;
import roomescape.domain.payment.Payment;

public interface PaymentRepository {
    Payment save(Payment payment);

    List<Payment> findAllByReservationIn(List<Reservation> reservations);

    void deleteByReservationId(long reservationId);
}
