package roomescape.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import roomescape.model.ReservationTime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservationTimeRepository extends CrudRepository<ReservationTime, Long> {

    List<ReservationTime> findAll();

    Optional<ReservationTime> findById(final Long id);

    ReservationTime save(final ReservationTime reservationTime);

    void deleteById(final Long id);

    boolean existsById(final Long id);

    boolean existsByStartAt(final LocalTime startAt);

    @Query("""
            SELECT r.time
            FROM Reservation r INNER JOIN ReservationTime t ON r.time.id = t.id
            WHERE r.date = :date AND r.theme.id = :themeId
            """)
    List<ReservationTime> findAllReservedTimes(@Param("date") final LocalDate date, @Param("themeId") final Long themeId);
}
