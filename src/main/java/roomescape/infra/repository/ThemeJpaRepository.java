package roomescape.infra.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import roomescape.domain.reservationdetail.Theme;

public interface ThemeJpaRepository extends Repository<Theme, Long> {

    Theme save(Theme theme);

    Optional<Theme> findById(Long id);

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

    List<Theme> findAll();

    void deleteById(Long themeId);
}
