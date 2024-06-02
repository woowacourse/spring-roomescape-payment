package roomescape.time.repository;

import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;

import roomescape.time.entity.ReservationTime;

public interface ReservationTimeRepository extends JpaRepository<ReservationTime, Long> {
    boolean existsByStartAt(LocalTime startAt);

}
