package roomescape.domain.reservationdetail;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservationTimeRepository {
    ReservationTime save(ReservationTime time);

    ReservationTime getReservationTime(Long id);

    Optional<ReservationTime> findReservationTime(Long id);

    List<ReservationTime> findAll();

    List<ReservationTime> findAllReservedTime(LocalDate date, Long themeId);

    List<ReservationTime> findAllUnAvailableTimes(LocalDate date, Long themeId);

    boolean existsByStartAt(LocalTime startAt);

    void delete(ReservationTime time);
}
