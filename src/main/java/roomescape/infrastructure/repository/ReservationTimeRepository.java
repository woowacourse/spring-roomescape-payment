package roomescape.infrastructure.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.exception.time.NotFoundReservationTimeException;

@Repository
public interface ReservationTimeRepository extends JpaRepository<ReservationTime, Long> {

    default ReservationTime getReservationTimeById(Long id) {
        return findById(id)
                .orElseThrow(NotFoundReservationTimeException::new);
    }

    @Query(value = """
            select
            t.id as reservation_time_id,
            t.start_at as time_value
            from reservation as r
            inner join reservation_time as t on r.reservation_time_id = t.id
            inner join theme as th on r.theme_id = th.id
            where r.date = :date
            and r.theme_id = :themeId
            """, nativeQuery = true)
    List<ReservationTime> findAllReservedTimeByDateAndThemeId(LocalDate date, Long themeId);

    boolean existsByStartAt(LocalTime startAt);
}
