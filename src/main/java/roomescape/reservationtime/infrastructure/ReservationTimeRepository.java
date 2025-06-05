package roomescape.reservationtime.infrastructure;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.presentation.dto.response.AvailableReservationTimeWebResponse;

public interface ReservationTimeRepository extends ListCrudRepository<ReservationTime, Long> {

    boolean existsByStartAt(LocalTime time);

    @Query("""
            SELECT DISTINCT new roomescape.reservationtime.presentation.dto.response.
                 AvailableReservationTimeWebResponse(rt.id, rt.startAt,
                     EXISTS (
                         SELECT 1 FROM ReservationSlot rs2
                         JOIN Reservation r ON r.reservationSlot.id = rs2.id
                         WHERE rs2.time.id = rt.id
                         AND rs2.date = :date
                         AND rs2.theme.id = :themeId
                         AND r.reservationStatus != 'FAILED'
                     )
                 )
                 FROM ReservationTime AS rt
                 ORDER BY rt.startAt
            """)
    List<AvailableReservationTimeWebResponse> findAvailable(LocalDate date, Long themeId);
}
