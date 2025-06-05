package roomescape.reservation.time.repository;

import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;

import roomescape.reservation.time.domain.ReservationTime;
import roomescape.reservation.time.domain.ReservationTimeId;

public interface ReservationTimeRepository extends JpaRepository<ReservationTime, ReservationTimeId> {

    boolean existsByStartAt(LocalTime startAt);

}
