package roomescape.theme.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import roomescape.theme.domain.Theme;

@Repository
public interface ThemeRepository extends ListCrudRepository<Theme, Long> {

    @Query("""
            SELECT t FROM Reservation AS r
            JOIN r.schedule.theme AS t
            WHERE r.schedule.date >= :startDate AND r.schedule.date <= :endDate
            GROUP BY t.id
            ORDER BY count(r.id) DESC
            LIMIT :limit
             """)
    List<Theme> findOrderByReservationCountDesc(LocalDate startDate, LocalDate endDate, int limit);
}
