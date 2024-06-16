package roomescape.infrastructure.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import roomescape.domain.reservationdetail.Theme;
import roomescape.exception.theme.NotFoundThemeException;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {

    default Theme getThemeById(Long id) {
        return findById(id).orElseThrow(NotFoundThemeException::new);
    }

    @Query(value = """
            select theme.id, theme.name, theme.description, theme.thumbnail
            from reservation
            left join theme on theme.id=reservation.theme_id
            where reservation.date >= ? and reservation.date <= ?
            group by theme.id
            order by count(*) desc
            limit ?;
            """, nativeQuery = true)
    List<Theme> findPopularThemes(String startDate, String endDate, int limit);
}
