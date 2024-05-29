package roomescape.infrastructure.reservationtime;

import java.time.LocalTime;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.schedule.ReservationTime;
import roomescape.domain.schedule.ReservationTimeRepository;

public interface ReservationTimeJpaRepository extends JpaRepository<ReservationTime, Long>, ReservationTimeRepository {
    boolean existsByStartAt(LocalTime startAt);
}
