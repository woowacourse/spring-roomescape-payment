package roomescape.infrastructure;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.business.model.entity.Theme;
import roomescape.business.model.vo.Id;

public interface ThemeRepository extends JpaRepository<Theme, Id> {

    @Query("""
            SELECT t
            FROM Theme t
            LEFT JOIN Reservation r ON r.theme.id = t.id AND r.date.value BETWEEN :dateFrom AND :dateTo
            GROUP BY t.id, t.name
            ORDER BY COUNT(r) DESC, t.name.value ASC
            """)
    List<Theme> findByDateBetweenOrderByReservationCountDescNameAsc(LocalDate dateFrom, LocalDate dateTo,
                                                                    Pageable pageable);
}
