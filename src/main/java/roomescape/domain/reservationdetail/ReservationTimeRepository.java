package roomescape.domain.reservationdetail;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservationTimeRepository {
    ReservationTime save(ReservationTime time);

    ReservationTime getReservationTime(Long id);

    List<ReservationTime> findAll();

    List<ReservationTime> findAllReservedTime(LocalDate date, Long themeId);

    boolean existsByStartAt(LocalTime startAt);

    void delete(Long id);
}
