package roomescape.infrastructure;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.business.model.entity.ReservationTime;
import roomescape.business.model.vo.Id;
import roomescape.presentation.dto.response.ReservationTimeResponseWithBooked;

public interface ReservationTimeRepository extends JpaRepository<ReservationTime, Id> {

    @Query("""
            SELECT new roomescape.presentation.dto.response.ReservationTimeResponseWithBooked(
                        rt.id.id,
                        rt.startTime.value,
                        CASE WHEN r.id IS NOT NULL THEN TRUE ELSE FALSE END AS already_booked
                    )
             FROM ReservationTime rt
             LEFT JOIN Reservation r
               ON rt.id = r.time
               AND r.date.value = :date
               AND r.theme.id = :themeId
            """)
    List<ReservationTimeResponseWithBooked> findByDateAndThemeIdWithAlreadyBooked(
            @Param("date") LocalDate date,
            @Param("themeId") Id themeId
    );

    boolean existsByStartTime_ValueBetween(LocalTime startInclusive, LocalTime endExclusive);

    boolean existsByStartTime_Value(LocalTime createTime);
}
