package roomescape.theme.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import roomescape.theme.domain.Theme;

@Tag(name = "테마 레포지토리", description = "테마 DB 데이터를 활용해 특정 값 반환")
public interface ThemeRepository extends CrudRepository<Theme, Long> {

    Theme save(Theme theme);

    List<Theme> findAll();

    Optional<Theme> findById(long id);

    @Query("SELECT r.theme " +
            "FROM Reservation r " +
            "WHERE r.date between :startDate AND :endDate " +
            "GROUP BY r.theme.id " +
            "ORDER BY COUNT(r.theme.id) DESC " +
            "LIMIT :limitCount "
    )
    List<Theme> findLimitedAllByDateOrderByThemeIdCount(LocalDate startDate, LocalDate endDate, int limitCount);

    void deleteById(long themeId);
}
