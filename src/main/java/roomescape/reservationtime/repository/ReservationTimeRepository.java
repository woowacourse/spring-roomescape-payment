package roomescape.reservationtime.repository;

import org.springframework.data.repository.Repository;
import roomescape.reservationtime.domain.ReservationTime;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservationTimeRepository extends Repository<ReservationTime, Long> {

    ReservationTime save(ReservationTime time);

    List<ReservationTime> findAllByOrderByStartAt();

    Optional<ReservationTime> findByStartAt(LocalTime startAt);

    Optional<ReservationTime> findById(Long id);

    void deleteById(Long timeId);
}
