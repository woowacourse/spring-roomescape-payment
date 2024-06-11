package roomescape.reservation.domain.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.reservation.domain.ReservationTime;

public interface ReservationTimeRepository extends JpaRepository<ReservationTime, Long> {

    boolean existsByStartAt(LocalTime time);

    @Query("""
                SELECT mr.reservation.time FROM MemberReservation mr
                WHERE mr.reservation.date = :date AND mr.reservation.theme.id = :themeId
            """)
    Set<ReservationTime> findReservedTime(@Param("date") LocalDate date, @Param("themeId") long themeId);
}
