package roomescape.infrastructure;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.business.model.entity.Reservation;
import roomescape.business.model.vo.Id;

public interface ReservationRepository extends JpaRepository<Reservation, Id> {

    @Query("""
            SELECT DISTINCT r
              FROM Reservation r
              JOIN FETCH r.timeSlot rt
              JOIN FETCH r.theme t
              JOIN FETCH r.member u
             WHERE (:themeId  IS NULL OR t.id = :themeId)
               AND (:userId   IS NULL OR u.id = :userId)
               AND (:dateFrom IS NULL OR r.date.value >= :dateFrom)
               AND (:dateTo   IS NULL OR r.date.value <= :dateTo)
            """)
    List<Reservation> findAllReservationWithFilter(Id themeId, Id userId, LocalDate dateFrom, LocalDate dateTo);

    boolean existsByTimeSlotId(Id timeId);

    boolean existsByThemeId(Id themeId);

    boolean existsByDate_ValueAndTimeSlot_StartAtAndThemeId(LocalDate date, LocalTime time, Id themeId);
}
