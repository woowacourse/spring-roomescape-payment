package roomescape.domain.schedule;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import roomescape.infrastructure.reservationtime.ReservationTimeJpaRepository;

public interface ReservationTimeRepository {
    boolean existsByStartAt(LocalTime startAt);

    Optional<ReservationTime> findById(long id);

    ReservationTime save(ReservationTime reservationTime);

    List<ReservationTime> findAll();

    void deleteById(long id);
}
