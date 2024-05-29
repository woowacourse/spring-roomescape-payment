package roomescape.domain.schedule;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservationTimeRepository {

    boolean existsByStartAt(LocalTime startAt);

    Optional<ReservationTime> findById(long id);

    ReservationTime save(ReservationTime reservationTime);

    List<ReservationTime> findAll();

    void deleteById(long id);
}
