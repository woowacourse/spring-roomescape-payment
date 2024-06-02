package roomescape.domain.reservationtime;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.exception.time.NotFoundTimeException;

import java.time.LocalTime;

public interface ReservationTimeRepository extends JpaRepository<ReservationTime, Long> {
    boolean existsByStartAt(LocalTime startAt);

    default ReservationTime getReservationTimeById(long id) {
        return findById(id)
                .orElseThrow(NotFoundTimeException::new);
    }
}
