package roomescape.infrastructure;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.business.model.entity.TimeSlot;
import roomescape.business.model.vo.Id;
import roomescape.presentation.dto.response.ReservationTimeResponseWithBooked;

public interface ReservationTimeRepository extends JpaRepository<TimeSlot, Id> {

    @Query("""
            SELECT new roomescape.presentation.dto.response.ReservationTimeResponseWithBooked(
                        rt.id.id,
                        rt.startAt,
                        CASE WHEN r.id IS NOT NULL THEN TRUE ELSE FALSE END AS already_booked
                    )
             FROM TimeSlot rt
             LEFT JOIN Reservation r
               ON rt.id = r.timeSlot
               AND r.date.value = :date
               AND r.theme.id = :themeId
            """)
    List<ReservationTimeResponseWithBooked> findByDateAndThemeIdWithAlreadyBooked(
            @Param("date") LocalDate date,
            @Param("themeId") Id themeId
    );

    boolean existsByStartAtBetween(LocalTime startInclusive, LocalTime endExclusive);

    boolean existsByStartAt(LocalTime createTime);
}
