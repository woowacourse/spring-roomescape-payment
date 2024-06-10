package roomescape.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.waiting.Waiting;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    Optional<Waiting> findByReservationId(Long id);

    List<Waiting> findByReservation_IdIn(List<Long> reservationIds);
}
