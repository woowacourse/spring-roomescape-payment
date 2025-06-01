package roomescape.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import roomescape.domain.reservationitem.ReservationTime;

import java.time.LocalTime;

@Repository
public interface ReservationTimeJpaRepository extends JpaRepository<ReservationTime, Long> {

    boolean existsByStartAt(LocalTime startAt);
}
