package roomescape.infrastructure.reservationtime;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.schedule.ReservationTime;
import roomescape.domain.schedule.ReservationTimeRepository;

import java.time.LocalTime;

public interface ReservationTimeJpaRepository extends JpaRepository<ReservationTime, Long>, ReservationTimeRepository {

    boolean existsByStartAt(LocalTime startAt);
}
