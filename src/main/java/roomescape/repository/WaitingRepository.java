package roomescape.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.domain.waiting.Waiting;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    Optional<Waiting> findByReservationId(Long id);

    @Query("""
              SELECT w
              FROM Waiting w
              WHERE w.reservation.id
              IN :reservationIds
            """)
    List<Waiting> findByReservationIdIn(List<Long> reservationIds);
}
