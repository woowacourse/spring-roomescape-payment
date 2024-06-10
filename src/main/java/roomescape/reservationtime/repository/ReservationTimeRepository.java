package roomescape.reservationtime.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;
import roomescape.reservationtime.domain.ReservationTime;

@Tag(name = "예약 시간 레포지토리", description = "예약 시간 DB를 활용해 특정 값 반환")
public interface ReservationTimeRepository extends Repository<ReservationTime, Long> {

    ReservationTime save(ReservationTime time);

    List<ReservationTime> findAllByOrderByStartAt();

    Optional<ReservationTime> findByStartAt(LocalTime startAt);

    Optional<ReservationTime> findById(Long id);

    void deleteById(Long timeId);
}
