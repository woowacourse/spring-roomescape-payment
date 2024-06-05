package roomescape.repository;

import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.time.ReservationTime;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomEscapeException;

public interface ReservationTimeRepository extends JpaRepository<ReservationTime, Long> {

    default ReservationTime findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new RoomEscapeException(
                ErrorCode.TIME_NOT_FOUND_BY_ID,
                "time_id = " + id
        ));
    }

    List<ReservationTime> findByIdNotIn(List<Long> ids);

    boolean existsByStartAt(LocalTime time);
}
