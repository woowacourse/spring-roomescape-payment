package roomescape.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.waiting.Waiting;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    Optional<Waiting> findByReservationId(Long id);

    Optional<Waiting> findByReservation(Reservation reservation);
}
