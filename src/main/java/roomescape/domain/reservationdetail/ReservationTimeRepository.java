package roomescape.domain.reservationdetail;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservationTimeRepository {
    ReservationTime save(ReservationTime time);

    default ReservationTime getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 시간입니다."));
    }

    Optional<ReservationTime> findById(Long id);

    List<ReservationTime> findAll();

    List<ReservationTime> findAllUnAvailableTimes(LocalDate date, Long themeId);

    boolean existsByStartAt(LocalTime startAt);

    void delete(ReservationTime time);
}
