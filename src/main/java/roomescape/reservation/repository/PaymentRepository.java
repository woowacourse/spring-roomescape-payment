package roomescape.reservation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import roomescape.reservation.domain.Payment;
import roomescape.reservation.domain.Reservation;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByReservation(Reservation reservation);

    List<Payment> findByReservationMemberId(long id);
}
