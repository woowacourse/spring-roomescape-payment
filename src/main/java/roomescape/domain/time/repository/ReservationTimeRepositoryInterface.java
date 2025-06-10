package roomescape.domain.time.repository;

import java.time.LocalTime;
import java.util.List;
import roomescape.domain.time.entity.ReservationTime;

public interface ReservationTimeRepositoryInterface {

    ReservationTime save(ReservationTime reservationTime);

    ReservationTime findById(Long id);

    boolean existsByStartAt(LocalTime startAt);

    List<ReservationTime> findAll();

    void deleteById(Long id);
}
