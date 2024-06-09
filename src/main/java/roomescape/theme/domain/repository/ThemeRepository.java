package roomescape.theme.domain.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.theme.domain.Theme;

public interface ThemeRepository extends JpaRepository<Theme, Long> {

    @Query(value = """
            SELECT t
                FROM Theme t
                RIGHT JOIN Reservation r ON t.id = r.theme.id
                WHERE r.date BETWEEN :startDate AND :endDate
                GROUP BY r.theme.id
                ORDER BY COUNT(r.theme.id) DESC, t.id ASC
                LIMIT :limit
            """)
    List<Theme> findTopNThemeBetweenStartDateAndEndDate(LocalDate startDate, LocalDate endDate, int limit);

    boolean existsByName(String name);

    @Query(value = """
            SELECT EXISTS(
                SELECT 1
                FROM Reservation r
                WHERE r.theme.id = :id
            )
            """)
    boolean isReservedTheme(Long id);
}
