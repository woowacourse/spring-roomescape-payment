package roomescape.theme.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.theme.domain.Theme;

public interface ThemeRepository extends JpaRepository<Theme, Long> {

    @Query(value = """
            SELECT
              t.id AS themeId,
              t.name AS themeName,
              t.description AS description,
              t.thumbnail AS thumbnail,
              t.price AS price
            FROM theme t
            JOIN (
              SELECT
                r.theme_id,
                COUNT(*) AS total_count
              FROM reservation r
              WHERE r.date BETWEEN DATEADD('DAY', -7, CURRENT_DATE) AND DATEADD('DAY', -1, CURRENT_DATE)
              GROUP BY r.theme_id
            
              UNION ALL
            
              SELECT
                w.theme_id,
                COUNT(*) AS total_count
              FROM waiting w
              WHERE 
                w.waiting_status IN ('PENDING', 'REJECTED', 'ACCEPTED')
                AND w.date BETWEEN DATEADD('DAY', -7, CURRENT_DATE) AND DATEADD('DAY', -1, CURRENT_DATE)
              GROUP BY w.theme_id
            ) AS combined
            ON t.id = combined.theme_id
            GROUP BY t.id, t.name, t.description, t.thumbnail, t.price
            ORDER BY SUM(combined.total_count) DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Theme> findByRank(@Param("limit") int limit);

    boolean existsByName(String name);
}
