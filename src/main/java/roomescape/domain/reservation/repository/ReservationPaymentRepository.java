package roomescape.domain.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.reservation.ReservationPayment;

import java.util.List;
import java.util.Optional;

public interface ReservationPaymentRepository extends JpaRepository<ReservationPayment, Long> {

    Optional<ReservationPayment> findByReservationId(Long reservationId);

    List<ReservationPayment> findAllByReservationIdIn(List<Long> reservationIds);
}
