package roomescape.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.reservation.domain.WaitingReservation;

public interface WaitingReservationRepository extends JpaRepository<WaitingReservation, Long> {
}
