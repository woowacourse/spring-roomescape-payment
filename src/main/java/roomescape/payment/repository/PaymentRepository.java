package roomescape.payment.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentId;
import roomescape.reservation.domain.ReservationId;

public interface PaymentRepository extends CrudRepository<Payment, PaymentId> {

    Optional<Payment> findByReservationId(ReservationId reservationId);

    boolean existsByReservationId(ReservationId reservationId);

    void deleteByReservationId(ReservationId reservationId);
}
